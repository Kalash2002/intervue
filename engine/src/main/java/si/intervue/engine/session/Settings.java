package si.intervue.engine.session;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import si.intervue.engine.store.Database;

/** Key-value settings in the {@code settings} table: calibration factors, the heartbeat. */
public final class Settings {

  static final String LAST_ALIVE_AT = "lastAliveAt";

  private final Database db;

  public Settings(Database db) {
    this.db = db;
  }

  public Optional<String> get(String key) {
    try (PreparedStatement s =
        db.connection().prepareStatement("SELECT value FROM settings WHERE key = ?")) {
      s.setString(1, key);
      try (ResultSet rs = s.executeQuery()) {
        return rs.next() ? Optional.of(rs.getString(1)) : Optional.empty();
      }
    } catch (SQLException e) {
      throw new IllegalStateException("Cannot read setting " + key, e);
    }
  }

  public void put(String key, String value) {
    try (PreparedStatement s =
        db.connection()
            .prepareStatement(
                "INSERT INTO settings(key, value) VALUES (?, ?) ON CONFLICT(key) DO UPDATE SET value = excluded.value")) {
      s.setString(1, key);
      s.setString(2, value);
      s.executeUpdate();
    } catch (SQLException e) {
      throw new IllegalStateException("Cannot write setting " + key, e);
    }
  }

  /** The calibration factor for a language; 1.0 until the Doctor has measured this machine. */
  public double calibrationFactor(String language) {
    return get("calibration." + language).map(Double::parseDouble).orElse(1.0);
  }
}
