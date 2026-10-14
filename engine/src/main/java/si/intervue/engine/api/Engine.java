package si.intervue.engine.api;

import java.nio.file.Path;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import si.intervue.engine.ai.AiClient;
import si.intervue.engine.ai.AiStatus;
import si.intervue.engine.ai.ClaudeCli;
import si.intervue.engine.ai.CliModelClient;
import si.intervue.engine.ai.UnavailableAiClient;
import si.intervue.engine.content.ContentStore;
import si.intervue.engine.content.PlainFolderSource;
import si.intervue.engine.health.Doctor;
import si.intervue.engine.health.EngineChecks;
import si.intervue.engine.health.HealthCheck;
import si.intervue.engine.languages.LanguageRegistry;
import si.intervue.engine.runners.ProcessControl;
import si.intervue.engine.sections.SectionRegistry;
import si.intervue.engine.session.SessionAssistant;
import si.intervue.engine.session.SessionStore;
import si.intervue.engine.session.Sessions;
import si.intervue.engine.session.edits.EditStore;
import si.intervue.engine.store.Database;
import si.intervue.engine.store.Settings;

/**
 * The composition root: one data folder (database and session work folders) and one problems
 * folder, wired into the use cases the HTTP layer calls. Owns the database connection and the lock
 * that guards it. It is the only code that may construct the Claude CLI route, and only in
 * developer mode (PLAN.md 2.5; ArchitectureTest).
 */
public final class Engine implements AutoCloseable {

  private final Database db;
  private final ContentStore content;
  private final Sessions sessions;
  private final Doctor doctor;
  private final boolean developerMode;
  private final AiClient ai;
  private final SessionAssistant assistant;
  private final ExecutorService reviews;
  private final ReentrantLock lock;

  private Engine(
      Database db,
      ContentStore content,
      Sessions sessions,
      Doctor doctor,
      boolean developerMode,
      AiClient ai,
      SessionAssistant assistant,
      ExecutorService reviews,
      ReentrantLock lock) {
    this.db = db;
    this.content = content;
    this.sessions = sessions;
    this.doctor = doctor;
    this.developerMode = developerMode;
    this.ai = ai;
    this.assistant = assistant;
    this.reviews = reviews;
    this.lock = lock;
  }

  public static Engine open(Path dataDir, Path problemsDir, Clock clock, boolean developerMode) {
    return open(dataDir, problemsDir, clock, developerMode, ClaudeCli::find);
  }

  /** {@code cli} finds the Claude CLI; it is asked only in developer mode (DeveloperModeTest). */
  public static Engine open(
      Path dataDir,
      Path problemsDir,
      Clock clock,
      boolean developerMode,
      Supplier<Optional<ClaudeCli>> cli) {
    return open(dataDir, problemsDir, clock, developerMode, route(developerMode, cli));
  }

  /** {@code ai} is the assistant's route; tests pass a recorded one instead of a model. */
  public static Engine open(
      Path dataDir, Path problemsDir, Clock clock, boolean developerMode, AiClient ai) {
    Database db = Database.open(dataDir.resolve("intervue.db"));
    Settings settings = new Settings(db);
    LanguageRegistry languages = LanguageRegistry.load();
    SectionRegistry sections = SectionRegistry.load();
    ProcessControl processes = new ProcessControl();
    ContentStore content = new PlainFolderSource(problemsDir);
    Sessions sessions =
        new Sessions(
            new SessionStore(db),
            new EditStore(db),
            settings,
            content,
            languages,
            sections,
            dataDir.resolve("sessions"),
            clock,
            processes);
    List<HealthCheck> checks =
        new ArrayList<>(EngineChecks.all(dataDir, settings, languages, sections, processes));
    if (developerMode) {
      checks.add(EngineChecks.assistant(ai::status));
    }
    Doctor doctor = new Doctor(db, checks, clock);
    ReentrantLock lock = new ReentrantLock();
    ExecutorService reviews =
        Executors.newSingleThreadExecutor(Thread.ofPlatform().name("review").daemon().factory());
    SessionAssistant assistant = new SessionAssistant(sessions, db, ai, reviews, lock);
    return new Engine(db, content, sessions, doctor, developerMode, ai, assistant, reviews, lock);
  }

  /** End users have no route before Phase 1b; developers get the CLI when it is installed. */
  private static AiClient route(boolean developerMode, Supplier<Optional<ClaudeCli>> finder) {
    if (!developerMode) {
      return new UnavailableAiClient(AiStatus.off());
    }
    return finder
        .get()
        .<AiClient>map(cli -> new CliModelClient(cli, CliModelClient.TIMEOUT_MS))
        .orElseGet(
            () ->
                new UnavailableAiClient(
                    new AiStatus(
                        true,
                        false,
                        "The Claude CLI was not found. Install Claude Code, then restart"
                            + " Intervue.")));
  }

  ContentStore content() {
    return content;
  }

  Sessions sessions() {
    return sessions;
  }

  Doctor doctor() {
    return doctor;
  }

  SessionAssistant assistant() {
    return assistant;
  }

  boolean developerMode() {
    return developerMode;
  }

  AiStatus aiStatus() {
    return developerMode ? ai.status() : AiStatus.off();
  }

  /** Runs {@code work} holding the store lock; every request does, except model streaming. */
  <T> T locked(Supplier<T> work) {
    lock.lock();
    try {
      return work.get();
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void close() {
    reviews.shutdownNow();
    db.close();
  }
}
