package si.intervue.engine.session;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import si.intervue.engine.languages.CompileResult;
import si.intervue.engine.languages.RunLimits;
import si.intervue.engine.languages.RunOutput;
import si.intervue.engine.languages.RunRequest;
import si.intervue.engine.languages.SourceDiagnostic;
import si.intervue.engine.runners.HeapPolicy;
import si.intervue.engine.runners.ProcessControl;
import si.intervue.engine.sections.PhaseResult;
import si.intervue.engine.sections.Score;
import si.intervue.engine.sections.ScoreInput;
import si.intervue.engine.sections.SessionType;
import si.intervue.engine.session.Sessions.Context;
import si.intervue.engine.store.Settings;
import si.intervue.problemformat.Phase;
import si.intervue.runprotocol.RunProtocolException;
import si.intervue.runprotocol.RunProtocolParser;
import si.intervue.runprotocol.RunResults;
import tools.jackson.core.type.TypeReference;

/**
 * Runs and finish: restore read-only files, compile, run, grade through the section, record the
 * run, mark first-time passes, unlock, and at finish score the session. Called only by {@link
 * Sessions}, which owns every other change.
 */
final class SessionRuns {

  private static final int OUTPUT_BYTES = 400_000;
  private static final long MAIN_WALL_MS = 20_000;

  /** What a finished session stores in {@code final_results}. */
  private record FinalResults(boolean compileError, List<PhaseResult> phases) {}

  /** One executed run before it is recorded. */
  private record Attempt(
      boolean compileError,
      boolean timedOut,
      String output,
      List<SourceDiagnostic> diagnostics,
      List<PhaseResult> phases) {}

  private final Sessions sessions;
  private final SessionStore store;
  private final Settings settings;
  private final ProcessControl processes;

  SessionRuns(Sessions sessions, SessionStore store, Settings settings, ProcessControl processes) {
    this.sessions = sessions;
    this.store = store;
    this.settings = settings;
    this.processes = processes;
  }

  RunView run(Context c, String target) {
    Attempt attempt = execute(c, target);
    String id = c.session().id();
    int activeEdits = store.activeEdits(id);
    List<Integer> newlyPassed = List.of();
    // A finished session can still be run for practice, but nothing is recorded any more.
    if (!c.session().finished()) {
      if (c.session().type() == SessionType.INTERVIEW && activeEdits > 0) {
        store.markAssisted(id);
      }
      sessions.event(c.session(), "run", runData(target, attempt, activeEdits));
      newlyPassed = markPassed(c, attempt.phases());
    }
    List<PhaseResult> shown =
        target.startsWith("phase:") ? onlyPhase(attempt.phases(), target) : attempt.phases();
    return new RunView(
        target,
        attempt.compileError(),
        attempt.timedOut(),
        attempt.output(),
        attempt.diagnostics(),
        shown,
        newlyPassed,
        store.find(id).orElseThrow().unlocked(),
        activeEdits);
  }

  void finish(Context c) {
    if (c.session().finished()) {
      return;
    }
    String id = c.session().id();
    Attempt attempt = execute(c, "all");
    int activeEdits = store.activeEdits(id);
    boolean assisted =
        c.session().assisted() || (c.session().type() == SessionType.INTERVIEW && activeEdits > 0);
    if (assisted) {
      store.markAssisted(id);
    }
    // A phase that first passes in this final run still counts as passed, at the finish time.
    markPassed(c, attempt.phases());
    sessions.event(c.session(), "finish", runData("all", attempt, activeEdits));
    SessionRecord s = store.find(id).orElseThrow();
    List<StoredEvent> events = store.events(id);
    long activeMs = sessions.activeMs(s, events);
    TimelineReport report = TimelineReport.of(events, activeMs);
    Score score = c.section().scoring().score(scoreInput(s, assisted, attempt.phases(), report));
    store.finish(
        id,
        sessions.now(),
        activeMs,
        SessionStore.JSON.writeValueAsString(
            new FinalResults(attempt.compileError(), attempt.phases())),
        SessionStore.JSON.writeValueAsString(score));
  }

  SessionResult result(Context c) {
    SessionRecord s = c.session();
    if (!s.finished()) {
      return null;
    }
    FinalResults finals =
        SessionStore.JSON.readValue(s.finalResultsJson(), new TypeReference<FinalResults>() {});
    Score score = SessionStore.JSON.readValue(s.scoreJson(), Score.class);
    Path walkthrough = c.problem().dir().resolve("walkthrough.md");
    return new SessionResult(
        score,
        finals.phases(),
        finals.compileError(),
        TimelineReport.of(store.events(s.id()), s.activeMs()),
        Files.exists(walkthrough) ? Sessions.readString(walkthrough) : "",
        c.problem().problem().followUps());
  }

  SessionSummary summary(SessionRecord s) {
    if (!s.finished()) {
      return new SessionSummary(
          s.id(), s.problemId(), s.type(), s.startedAt(), false, null, null, false, false);
    }
    Score score = SessionStore.JSON.readValue(s.scoreJson(), Score.class);
    // ADR 0006: every passing Interview session is solved, with or without AI edits.
    boolean counts = s.type() == SessionType.INTERVIEW && score.solved();
    return new SessionSummary(
        s.id(),
        s.problemId(),
        s.type(),
        s.startedAt(),
        true,
        score.total(),
        score.label(),
        score.solved(),
        counts);
  }

  private Attempt execute(Context c, String target) {
    List<Phase> phases = c.problem().problem().phases();
    long wallRefMs = wallRefMs(phases, target);
    Path work = c.work().root();
    Path out = work.getParent().resolve("out");
    c.work().restoreReadOnly();
    c.language().testKit().install(work);
    CompileResult compiled = c.language().runner().compile(work, out);
    var variant = c.problem().variants().get(c.session().language());
    if (!compiled.ok()) {
      return new Attempt(
          true,
          false,
          compiled.output(),
          compiled.diagnostics(),
          target.equals("main")
              ? List.of()
              : c.section().grader().grade(c.problem().problem(), variant, null));
    }
    double factor = settings.calibrationFactor(c.session().language());
    RunLimits limits =
        new RunLimits(Math.round(wallRefMs * factor), HeapPolicy.defaultMb(), OUTPUT_BYTES);
    RunOutput ran =
        c.language()
            .runner()
            .run(new RunRequest(work, out, target, variant, limits, factor), processes);
    if (target.equals("main")) {
      return new Attempt(false, ran.timedOut(), ran.output(), compiled.diagnostics(), List.of());
    }
    RunResults results = readResults(ran);
    return new Attempt(
        false,
        ran.timedOut(),
        ran.output(),
        compiled.diagnostics(),
        c.section().grader().grade(c.problem().problem(), variant, results));
  }

  /** The whole-run cap: the phase's own cap, the sum for {@code all}; refuses unknown targets. */
  private static long wallRefMs(List<Phase> phases, String target) {
    if (target.equals("main")) {
      return MAIN_WALL_MS;
    }
    if (target.equals("all")) {
      return phases.stream().mapToLong(Phase::timeoutRefMs).sum();
    }
    if (target.startsWith("phase:")) {
      for (Phase phase : phases) {
        if (target.equals("phase:" + phase.id())) {
          return phase.timeoutRefMs();
        }
      }
    }
    throw new SessionException("Unknown run target: " + target);
  }

  // A run killed before the kit wrote its first line has no results; it grades as no passes.
  private static RunResults readResults(RunOutput ran) {
    try {
      return RunProtocolParser.parse(ran.resultsFile());
    } catch (RunProtocolException e) {
      return null;
    }
  }

  private List<Integer> markPassed(Context c, List<PhaseResult> phases) {
    String id = c.session().id();
    Map<Integer, Long> passed = Sessions.phasesPassed(store.events(id));
    List<Integer> newly = new ArrayList<>();
    for (PhaseResult phase : phases) {
      if (phase.green() && !passed.containsKey(phase.phaseId())) {
        sessions.event(c.session(), "phasePassed", Map.of("phase", phase.phaseId()));
        passed.put(phase.phaseId(), 0L);
        newly.add(phase.phaseId());
      }
    }
    // A phase opens when every phase before it has passed.
    int unlocked = store.find(id).orElseThrow().unlocked();
    int count = c.problem().problem().phases().size();
    while (unlocked < count && passed.containsKey(unlocked)) {
      unlocked++;
    }
    store.setUnlocked(id, unlocked);
    return newly;
  }

  private static Map<String, Object> runData(String target, Attempt attempt, int activeEdits) {
    int passed = attempt.phases().stream().mapToInt(PhaseResult::passed).sum();
    int failed =
        attempt.phases().stream().mapToInt(p -> p.failed() + p.timeouts() + p.notRun()).sum();
    return Map.of(
        "target",
        target,
        "compileError",
        attempt.compileError(),
        "passed",
        passed,
        "failed",
        failed,
        "aiEditsActive",
        activeEdits);
  }

  private static List<PhaseResult> onlyPhase(List<PhaseResult> phases, String target) {
    return phases.stream().filter(p -> target.equals("phase:" + p.phaseId())).toList();
  }

  private static ScoreInput scoreInput(
      SessionRecord s, boolean assisted, List<PhaseResult> finals, TimelineReport r) {
    return new ScoreInput(
        s.type(),
        assisted,
        s.minutes(),
        finals,
        r.activeMs(),
        r.runs(),
        r.prompts(),
        r.longestGapWithoutRunMs(),
        r.longestPromptStreak(),
        r.phasesSkipped());
  }
}
