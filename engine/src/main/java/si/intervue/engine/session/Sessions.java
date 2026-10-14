package si.intervue.engine.session;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import si.intervue.engine.content.ContentException;
import si.intervue.engine.content.ContentStore;
import si.intervue.engine.languages.LanguagePackage;
import si.intervue.engine.languages.LanguageRegistry;
import si.intervue.engine.runners.ProcessControl;
import si.intervue.engine.sections.SectionPackage;
import si.intervue.engine.sections.SectionRegistry;
import si.intervue.engine.sections.SessionType;
import si.intervue.problemformat.ProblemFolder;

/**
 * The session use cases (PLAN.md 2.8): create, files, notes, skip, the clock with pauses, and,
 * through {@link SessionRuns}, runs and finish. Every change to a session goes through here; the
 * store only persists and the work folder only guards paths.
 */
public final class Sessions {

  private static final DateTimeFormatter ID_TIME =
      DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").withZone(ZoneOffset.UTC);

  private final SessionStore store;
  private final Settings settings;
  private final ContentStore content;
  private final LanguageRegistry languages;
  private final SectionRegistry sections;
  private final Path sessionsRoot;
  private final Clock clock;
  private final SessionRuns runs;

  public Sessions(
      SessionStore store,
      Settings settings,
      ContentStore content,
      LanguageRegistry languages,
      SectionRegistry sections,
      Path sessionsRoot,
      Clock clock,
      ProcessControl processes) {
    this.store = store;
    this.settings = settings;
    this.content = content;
    this.languages = languages;
    this.sections = sections;
    this.sessionsRoot = sessionsRoot;
    this.clock = clock;
    this.runs = new SessionRuns(this, store, settings, processes);
  }

  /** A session with everything its rules need. */
  record Context(
      SessionRecord session,
      ProblemFolder problem,
      LanguagePackage language,
      SectionPackage section,
      WorkFolder work) {}

  public SessionView create(String problemId, SessionType type) {
    ProblemFolder problem;
    try {
      problem = content.load(problemId);
    } catch (ContentException e) {
      throw new SessionException(e.getMessage());
    }
    String language = problem.problem().languages().get(0);
    LanguagePackage pkg = requireLanguage(language);
    SectionPackage section = requireSection(problem.problem().section());
    List<String> refused = section.schema().validate(problem);
    if (!refused.isEmpty()) {
      throw new SessionException(
          "Problem " + problemId + " cannot be used: " + String.join("; ", refused));
    }
    String id = newId(problemId);
    WorkFolder work = workFolder(id, problem, language, pkg);
    work.createFromStarter();
    pkg.testKit().install(work.root());
    store.insert(
        new SessionRecord(
            id,
            problemId,
            problem.problem().version(),
            language,
            section.definition().id(),
            type,
            clock.millis(),
            null,
            problem.problem().minutes(),
            0,
            1,
            false,
            Map.of(),
            null,
            null,
            ""));
    return view(id);
  }

  public SessionView view(String id) {
    Context c = context(id);
    SessionRecord s = c.session();
    List<StoredEvent> events = store.events(id);
    return new SessionView(
        s.id(),
        s.problemId(),
        c.problem().problem().title(),
        s.type(),
        s.minutes(),
        activeMs(s, events),
        s.finished(),
        s.assisted(),
        s.unlocked(),
        c.problem().problem().phases().size(),
        phasesPassed(events),
        s.hintsUsed(),
        store.activeEdits(id),
        brief(c),
        s.notes(),
        events.stream().map(e -> new EventView(e.seq(), e.tMs(), e.type(), e.data())).toList(),
        runs.result(c));
  }

  public List<FileEntry> files(String id) {
    return context(id).work().list();
  }

  public String readFile(String id, String path) {
    return context(id).work().read(path);
  }

  public void writeFile(String id, String path, String text) {
    Context c = requireOpen(id);
    c.work().write(path, text);
  }

  public void createFile(String id, String path) {
    requireOpen(id).work().create(path);
  }

  public void saveNotes(String id, String notes) {
    context(id);
    store.setNotes(id, notes, clock.millis());
  }

  /** Opens the next phase before the current one passes; recorded, and it costs rhythm points. */
  public SessionView skip(String id) {
    Context c = requireOpen(id);
    int phases = c.problem().problem().phases().size();
    if (c.session().unlocked() < phases) {
      int next = c.session().unlocked() + 1;
      store.setUnlocked(id, next);
      event(c.session(), "skip", Map.of("phase", next));
    }
    return view(id);
  }

  public RunView run(String id, String target) {
    return runs.run(context(id), target);
  }

  public SessionView finish(String id) {
    runs.finish(context(id));
    return view(id);
  }

  /** Every session, newest first, for the dashboard and history. */
  public List<SessionSummary> history() {
    return store.all().stream().map(runs::summary).toList();
  }

  // ---- the clock (PLAN.md 2.8, "Clock and pauses") ----

  /** Called every 15 seconds while the engine runs; the last value bounds a crash's pause. */
  public void heartbeat() {
    settings.put(Settings.LAST_ALIVE_AT, String.valueOf(clock.millis()));
  }

  /** Closes the gap left by the last shutdown or crash in every unfinished session. */
  public void onStartup() {
    long now = clock.millis();
    Long lastAlive = settings.get(Settings.LAST_ALIVE_AT).map(Long::parseLong).orElse(null);
    for (String id : store.unfinishedIds()) {
      SessionRecord s = store.find(id).orElseThrow();
      List<StoredEvent> events = store.events(id);
      if (!SessionClock.isPaused(events)) {
        if (lastAlive == null || lastAlive <= s.startedAt()) {
          continue;
        }
        appendClockEvent(s, events, SessionClock.PAUSE, Math.min(lastAlive, now));
        events = store.events(id);
      }
      appendClockEvent(s, events, SessionClock.RESUME, now);
    }
    heartbeat();
  }

  /** A clean shutdown pauses every unfinished session at this moment. */
  public void onShutdown() {
    long now = clock.millis();
    for (String id : store.unfinishedIds()) {
      SessionRecord s = store.find(id).orElseThrow();
      List<StoredEvent> events = store.events(id);
      if (!SessionClock.isPaused(events)) {
        appendClockEvent(s, events, SessionClock.PAUSE, now);
      }
    }
    heartbeat();
  }

  private void appendClockEvent(SessionRecord s, List<StoredEvent> events, String type, long at) {
    store.appendEvent(
        s.id(), SessionClock.activeMs(s.startedAt(), events, at), type, Map.of("at", at));
  }

  // ---- helpers shared with SessionRuns ----

  long activeMs(SessionRecord s, List<StoredEvent> events) {
    if (s.finished()) {
      return s.activeMs();
    }
    return SessionClock.activeMs(s.startedAt(), events, clock.millis());
  }

  long now() {
    return clock.millis();
  }

  void event(SessionRecord s, String type, Map<String, Object> data) {
    store.appendEvent(s.id(), activeMs(s, store.events(s.id())), type, data);
  }

  Context context(String id) {
    SessionRecord s =
        store.find(id).orElseThrow(() -> new SessionException("No such session: " + id));
    ProblemFolder problem;
    try {
      problem = content.load(s.problemId());
    } catch (ContentException e) {
      throw new SessionException(e.getMessage());
    }
    LanguagePackage pkg = requireLanguage(s.language());
    return new Context(
        s, problem, pkg, requireSection(s.sectionId()), workFolder(id, problem, s.language(), pkg));
  }

  Context requireOpen(String id) {
    Context c = context(id);
    if (c.session().finished()) {
      throw new SessionException("This session is finished. Files are read-only.");
    }
    return c;
  }

  static Map<Integer, Long> phasesPassed(List<StoredEvent> events) {
    Map<Integer, Long> passed = new LinkedHashMap<>();
    for (StoredEvent e : events) {
      if (e.type().equals("phasePassed")) {
        passed.putIfAbsent((int) e.number("phase"), e.tMs());
      }
    }
    return passed;
  }

  private WorkFolder workFolder(
      String id, ProblemFolder problem, String language, LanguagePackage pkg) {
    Path variant = problem.variantDir(language);
    return new WorkFolder(
        sessionsRoot.resolve(id).resolve("work"),
        variant.resolve("starter"),
        problem.variants().get(language).readOnly(),
        pkg.testKit().files(),
        pkg.definition().extensions());
  }

  /** Intro, then each phase's brief while unlocked (all of them after finish). */
  private static List<String> brief(Context c) {
    List<String> parts = new ArrayList<>();
    Path dir = c.problem().dir();
    parts.add(readString(dir.resolve("brief/intro.md")));
    for (var phase : c.problem().problem().phases()) {
      boolean open = c.session().finished() || phase.id() <= c.session().unlocked();
      parts.add(open ? readString(dir.resolve(phase.brief())) : null);
    }
    return parts;
  }

  static String readString(Path file) {
    try {
      return Files.readString(file, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private String newId(String problemId) {
    String base = problemId + "-" + ID_TIME.format(clock.instant());
    String id = base;
    for (int n = 2; store.find(id).isPresent(); n++) {
      id = base + "-" + n;
    }
    return id;
  }

  private LanguagePackage requireLanguage(String id) {
    return languages
        .find(id)
        .orElseThrow(() -> new SessionException("Language " + id + " is not installed."));
  }

  private SectionPackage requireSection(String id) {
    return sections
        .find(id)
        .orElseThrow(() -> new SessionException("Section " + id + " is not installed."));
  }
}
