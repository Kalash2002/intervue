package si.intervue.engine.session;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import si.intervue.engine.sections.SessionType;
import si.intervue.engine.store.Database;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * SQL for sessions and their append-only timeline. Holds no rules: what may change and when is
 * decided by {@link Sessions}. Events are only ever appended.
 */
public final class SessionStore {

  static final ObjectMapper JSON = JsonMapper.builder().build();
  private static final TypeReference<Map<String, Object>> MAP = new TypeReference<>() {};
  private static final TypeReference<Map<Integer, Integer>> HINTS = new TypeReference<>() {};

  private final Database db;

  public SessionStore(Database db) {
    this.db = db;
  }

  void insert(SessionRecord s) {
    String sql =
        "INSERT INTO sessions(id, problem_id, problem_version, variant_lang, section_id, session_type,"
            + " started_at, minutes, unlocked) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    try (PreparedStatement ps = db.connection().prepareStatement(sql)) {
      ps.setString(1, s.id());
      ps.setString(2, s.problemId());
      ps.setInt(3, s.problemVersion());
      ps.setString(4, s.language());
      ps.setString(5, s.sectionId());
      ps.setString(6, s.type().name().toLowerCase(Locale.ROOT));
      ps.setLong(7, s.startedAt());
      ps.setInt(8, s.minutes());
      ps.setInt(9, s.unlocked());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new IllegalStateException("Cannot create session " + s.id(), e);
    }
  }

  Optional<SessionRecord> find(String id) {
    List<SessionRecord> found = query("SELECT * FROM sessions WHERE id = ?", id);
    return found.isEmpty() ? Optional.empty() : Optional.of(found.get(0));
  }

  List<SessionRecord> all() {
    return query("SELECT * FROM sessions ORDER BY started_at DESC");
  }

  List<String> unfinishedIds() {
    return query("SELECT * FROM sessions WHERE finished_at IS NULL").stream()
        .map(SessionRecord::id)
        .toList();
  }

  void setUnlocked(String id, int unlocked) {
    update("UPDATE sessions SET unlocked = ? WHERE id = ?", unlocked, id);
  }

  void setHintsUsed(String id, Map<Integer, Integer> hints) {
    update("UPDATE sessions SET hints_used = ? WHERE id = ?", JSON.writeValueAsString(hints), id);
  }

  void setNotes(String id, String notes, long at) {
    update("UPDATE sessions SET notes = ?, notes_updated_at = ? WHERE id = ?", notes, at, id);
  }

  void markAssisted(String id) {
    update("UPDATE sessions SET assisted = 1 WHERE id = ?", id);
  }

  void finish(
      String id, long finishedAt, long activeMs, String finalResultsJson, String scoreJson) {
    update(
        "UPDATE sessions SET finished_at = ?, active_ms = ?, final_results = ?, score = ? WHERE id = ?",
        finishedAt,
        activeMs,
        finalResultsJson,
        scoreJson,
        id);
  }

  /** Appends an event with the next sequence number of its session. */
  void appendEvent(String id, long tMs, String type, Map<String, Object> data) {
    String sql =
        "INSERT INTO events(session_id, seq, t_ms, type, data) VALUES (?, (SELECT COALESCE(MAX(seq), 0) + 1"
            + " FROM events WHERE session_id = ?), ?, ?, ?)";
    update(sql, id, id, tMs, type, JSON.writeValueAsString(data));
  }

  List<StoredEvent> events(String id) {
    List<StoredEvent> events = new ArrayList<>();
    try (PreparedStatement ps =
        db.connection()
            .prepareStatement(
                "SELECT seq, t_ms, type, data FROM events WHERE session_id = ? ORDER BY seq")) {
      ps.setString(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          events.add(
              new StoredEvent(
                  rs.getInt(1),
                  rs.getLong(2),
                  rs.getString(3),
                  JSON.readValue(rs.getString(4), MAP)));
        }
      }
    } catch (SQLException e) {
      throw new IllegalStateException("Cannot read events of " + id, e);
    }
    return events;
  }

  /** AI edits applied in this session and not undone (PLAN.md 2.8, {@code aiEditsActive}). */
  int activeEdits(String id) {
    try (PreparedStatement ps =
        db.connection()
            .prepareStatement(
                "SELECT COUNT(*) FROM edits WHERE session_id = ? AND undone_at IS NULL")) {
      ps.setString(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        rs.next();
        return rs.getInt(1);
      }
    } catch (SQLException e) {
      throw new IllegalStateException("Cannot count edits of " + id, e);
    }
  }

  private List<SessionRecord> query(String sql, Object... args) {
    List<SessionRecord> rows = new ArrayList<>();
    try (PreparedStatement ps = db.connection().prepareStatement(sql)) {
      bind(ps, args);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          rows.add(toRecord(rs));
        }
      }
    } catch (SQLException e) {
      throw new IllegalStateException("Cannot read sessions", e);
    }
    return rows;
  }

  private static SessionRecord toRecord(ResultSet rs) throws SQLException {
    long finished = rs.getLong("finished_at");
    Long finishedAt = rs.wasNull() ? null : finished;
    return new SessionRecord(
        rs.getString("id"),
        rs.getString("problem_id"),
        rs.getInt("problem_version"),
        rs.getString("variant_lang"),
        rs.getString("section_id"),
        SessionType.valueOf(rs.getString("session_type").toUpperCase(Locale.ROOT)),
        rs.getLong("started_at"),
        finishedAt,
        rs.getInt("minutes"),
        rs.getLong("active_ms"),
        rs.getInt("unlocked"),
        rs.getInt("assisted") == 1,
        new LinkedHashMap<>(JSON.readValue(rs.getString("hints_used"), HINTS)),
        rs.getString("final_results"),
        rs.getString("score"),
        rs.getString("notes"));
  }

  private void update(String sql, Object... args) {
    try (PreparedStatement ps = db.connection().prepareStatement(sql)) {
      bind(ps, args);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new IllegalStateException("Cannot update sessions", e);
    }
  }

  private static void bind(PreparedStatement ps, Object... args) throws SQLException {
    for (int i = 0; i < args.length; i++) {
      ps.setObject(i + 1, args[i]);
    }
  }
}
