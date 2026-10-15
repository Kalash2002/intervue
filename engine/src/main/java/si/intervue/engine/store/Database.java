package si.intervue.engine.store;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * The engine's SQLite database (ADR 0004): opens it with WAL and foreign keys, and brings the
 * schema up to date with forward-only migrations, each in its own transaction. Repositories use
 * {@link #connection()}; nothing else talks to SQLite.
 */
public final class Database implements AutoCloseable {

  /**
   * Migrations in order, as classpath resources under {@code db/migration/}; version N is the Nth
   * entry. Append only: a shipped migration is never edited or reordered.
   */
  public static final List<String> MIGRATIONS = List.of("V1__init.sql");

  private final Connection connection;

  private Database(Connection connection) {
    this.connection = connection;
  }

  public static Database open(Path file) {
    return open(file, MIGRATIONS);
  }

  static Database open(Path file, List<String> migrations) {
    try {
      Files.createDirectories(file.toAbsolutePath().getParent());
      Connection connection =
          DriverManager.getConnection(
              "jdbc:sqlite:" + file.toAbsolutePath() + "?journal_mode=WAL&foreign_keys=true");
      Database db = new Database(connection);
      try {
        db.migrate(migrations);
      } catch (RuntimeException e) {
        db.close();
        throw e;
      }
      return db;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } catch (SQLException e) {
      throw new IllegalStateException("Cannot open the database at " + file, e);
    }
  }

  public Connection connection() {
    return connection;
  }

  public int schemaVersion() {
    try (Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery("SELECT COALESCE(MAX(version), 0) FROM schema_version")) {
      rs.next();
      return rs.getInt(1);
    } catch (SQLException e) {
      throw new IllegalStateException("Cannot read the schema version", e);
    }
  }

  private void migrate(List<String> migrations) throws SQLException {
    try (Statement s = connection.createStatement()) {
      s.executeUpdate(
          "CREATE TABLE IF NOT EXISTS schema_version (version INTEGER PRIMARY KEY, applied_at INTEGER NOT NULL)");
    }
    int current = schemaVersion();
    if (current > migrations.size()) {
      throw new IllegalStateException(
          "This database was written by a newer version of Intervue (schema "
              + current
              + "); update the app to open it.");
    }
    for (int version = current + 1; version <= migrations.size(); version++) {
      apply(version, migrations.get(version - 1));
    }
  }

  private void apply(int version, String name) throws SQLException {
    String sql = readMigration(name);
    connection.setAutoCommit(false);
    try (Statement s = connection.createStatement()) {
      // Migrations are plain DDL with no semicolons inside strings, so splitting on ';' is safe.
      for (String statement : stripComments(sql).split(";")) {
        if (!statement.isBlank()) {
          s.executeUpdate(statement);
        }
      }
      try (PreparedStatement record =
          connection.prepareStatement(
              "INSERT INTO schema_version(version, applied_at) VALUES (?, ?)")) {
        record.setInt(1, version);
        record.setLong(2, System.currentTimeMillis());
        record.executeUpdate();
      }
      connection.commit();
    } catch (SQLException e) {
      connection.rollback();
      throw new IllegalStateException(
          "Migration " + name + " failed; the database is unchanged", e);
    } finally {
      connection.setAutoCommit(true);
    }
  }

  private static String readMigration(String name) {
    try (InputStream in = Database.class.getResourceAsStream("/db/migration/" + name)) {
      if (in == null) {
        throw new IllegalStateException("Migration " + name + " is missing from the engine");
      }
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static String stripComments(String sql) {
    StringBuilder out = new StringBuilder();
    for (String line : sql.split("\n")) {
      if (!line.strip().startsWith("--")) {
        out.append(line).append('\n');
      }
    }
    return out.toString();
  }

  @Override
  public void close() {
    try {
      connection.close();
    } catch (SQLException e) {
      throw new IllegalStateException("Cannot close the database", e);
    }
  }
}
