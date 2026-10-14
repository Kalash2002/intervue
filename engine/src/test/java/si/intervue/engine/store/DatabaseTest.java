package si.intervue.engine.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DatabaseTest {

  @TempDir Path dir;

  private static List<String> tables(Connection c) throws SQLException {
    List<String> names = new ArrayList<>();
    try (Statement s = c.createStatement();
        ResultSet rs =
            s.executeQuery("SELECT name FROM sqlite_master WHERE type = 'table' ORDER BY name")) {
      while (rs.next()) {
        names.add(rs.getString(1));
      }
    }
    return names;
  }

  private static String pragma(Connection c, String name) throws SQLException {
    try (Statement s = c.createStatement();
        ResultSet rs = s.executeQuery("PRAGMA " + name)) {
      return rs.next() ? rs.getString(1) : null;
    }
  }

  @Test
  void aFreshDatabaseGetsTheVersionOneSchema() throws SQLException {
    try (Database db = Database.open(dir.resolve("intervue.db"))) {
      assertThat(tables(db.connection()))
          .contains(
              "sessions",
              "events",
              "edits",
              "chat_turns",
              "session_files",
              "settings",
              "health_results",
              "schema_version");
      assertThat(db.schemaVersion()).isEqualTo(Database.MIGRATIONS.size());
    }
  }

  @Test
  void opensWithWalAndForeignKeysOn() throws SQLException {
    try (Database db = Database.open(dir.resolve("intervue.db"))) {
      assertThat(pragma(db.connection(), "journal_mode")).isEqualToIgnoringCase("wal");
      assertThat(pragma(db.connection(), "foreign_keys")).isEqualTo("1");
    }
  }

  @Test
  void reopeningAppliesNothingTwiceAndKeepsData() throws SQLException {
    Path file = dir.resolve("intervue.db");
    try (Database db = Database.open(file)) {
      try (Statement s = db.connection().createStatement()) {
        s.executeUpdate("INSERT INTO settings(key, value) VALUES ('theme', 'dark')");
      }
    }
    try (Database db = Database.open(file);
        Statement s = db.connection().createStatement();
        ResultSet rs = s.executeQuery("SELECT value FROM settings WHERE key = 'theme'")) {
      assertThat(rs.next()).isTrue();
      assertThat(rs.getString(1)).isEqualTo("dark");
      assertThat(db.schemaVersion()).isEqualTo(Database.MIGRATIONS.size());
    }
  }

  @Test
  void aLaterMigrationAppliesOnTopOfAnExistingDatabase() throws SQLException {
    Path file = dir.resolve("intervue.db");
    try (Database db = Database.open(file)) {
      assertThat(db.schemaVersion()).isEqualTo(Database.MIGRATIONS.size());
    }
    List<String> withV2 = new ArrayList<>(Database.MIGRATIONS);
    withV2.add("test/Vnext__add_column.sql");
    try (Database db = Database.open(file, withV2)) {
      assertThat(db.schemaVersion()).isEqualTo(Database.MIGRATIONS.size() + 1);
      try (Statement s = db.connection().createStatement()) {
        s.executeUpdate("INSERT INTO settings(key, value, note) VALUES ('a', 'b', 'added by v2')");
      }
    }
  }

  @Test
  void refusesADatabaseNewerThanThisEngine() throws SQLException {
    Path file = dir.resolve("intervue.db");
    List<String> withV2 = new ArrayList<>(Database.MIGRATIONS);
    withV2.add("test/Vnext__add_column.sql");
    Database.open(file, withV2).close();

    assertThatThrownBy(() -> Database.open(file))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("newer");
  }

  @Test
  void aFailedMigrationLeavesTheDatabaseAtThePreviousVersion() throws SQLException {
    Path file = dir.resolve("intervue.db");
    Database.open(file).close();
    List<String> broken = new ArrayList<>(Database.MIGRATIONS);
    broken.add("test/Vnext__broken.sql");

    assertThatThrownBy(() -> Database.open(file, broken)).isInstanceOf(IllegalStateException.class);
    try (Database db = Database.open(file)) {
      assertThat(db.schemaVersion()).isEqualTo(Database.MIGRATIONS.size());
      assertThat(tables(db.connection())).doesNotContain("half_done");
    }
  }
}
