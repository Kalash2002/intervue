package si.intervue.engine.api;

import java.nio.file.Path;
import java.time.Clock;
import si.intervue.engine.ai.AiStatus;
import si.intervue.engine.content.ContentStore;
import si.intervue.engine.content.PlainFolderSource;
import si.intervue.engine.health.Doctor;
import si.intervue.engine.health.EngineChecks;
import si.intervue.engine.languages.LanguageRegistry;
import si.intervue.engine.runners.ProcessControl;
import si.intervue.engine.sections.SectionRegistry;
import si.intervue.engine.session.SessionStore;
import si.intervue.engine.session.Sessions;
import si.intervue.engine.session.edits.EditStore;
import si.intervue.engine.store.Database;
import si.intervue.engine.store.Settings;

/**
 * The composition root: one data folder (database and session work folders) and one problems
 * folder, wired into the use cases the HTTP layer calls. Owns the database connection.
 */
public final class Engine implements AutoCloseable {

  private final Database db;
  private final ContentStore content;
  private final Sessions sessions;
  private final Doctor doctor;
  private final boolean developerMode;

  private Engine(
      Database db, ContentStore content, Sessions sessions, Doctor doctor, boolean developerMode) {
    this.db = db;
    this.content = content;
    this.sessions = sessions;
    this.doctor = doctor;
    this.developerMode = developerMode;
  }

  public static Engine open(Path dataDir, Path problemsDir, Clock clock, boolean developerMode) {
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
    Doctor doctor =
        new Doctor(db, EngineChecks.all(dataDir, settings, languages, sections, processes), clock);
    return new Engine(db, content, sessions, doctor, developerMode);
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

  boolean developerMode() {
    return developerMode;
  }

  AiStatus aiStatus() {
    return developerMode
        ? new AiStatus(true, false, "The Claude CLI route is not wired yet")
        : AiStatus.off();
  }

  @Override
  public void close() {
    db.close();
  }
}
