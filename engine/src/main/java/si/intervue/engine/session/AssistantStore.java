package si.intervue.engine.session;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import si.intervue.engine.store.Database;

/**
 * The assistant's records: chat turns in {@code chat_turns}, the review in {@code
 * sessions.feedback}.
 */
final class AssistantStore {

  private final Database db;

  AssistantStore(Database db) {
    this.db = db;
  }

  void appendTurn(String sessionId, String role, String text, String model, long tMs) {
    String sql =
        "INSERT INTO chat_turns(session_id, seq, role, text, model, t_ms) VALUES (?,"
            + " (SELECT COALESCE(MAX(seq), 0) + 1 FROM chat_turns WHERE session_id = ?), ?, ?, ?, ?)";
    try (PreparedStatement ps = db.connection().prepareStatement(sql)) {
      ps.setString(1, sessionId);
      ps.setString(2, sessionId);
      ps.setString(3, role);
      ps.setString(4, text);
      ps.setString(5, model);
      ps.setLong(6, tMs);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new IllegalStateException("Cannot store a chat turn", e);
    }
  }

  List<SessionAssistant.Turn> turns(String sessionId) {
    List<SessionAssistant.Turn> turns = new ArrayList<>();
    String sql = "SELECT role, text, t_ms FROM chat_turns WHERE session_id = ? ORDER BY seq";
    try (PreparedStatement ps = db.connection().prepareStatement(sql)) {
      ps.setString(1, sessionId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          turns.add(new SessionAssistant.Turn(rs.getString(1), rs.getString(2), rs.getLong(3)));
        }
      }
    } catch (SQLException e) {
      throw new IllegalStateException("Cannot read chat turns", e);
    }
    return turns;
  }

  /** The stored review as JSON, or null while none has been written. */
  String review(String sessionId) {
    try (PreparedStatement ps =
        db.connection().prepareStatement("SELECT feedback FROM sessions WHERE id = ?")) {
      ps.setString(1, sessionId);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? rs.getString(1) : null;
      }
    } catch (SQLException e) {
      throw new IllegalStateException("Cannot read the review", e);
    }
  }

  void setReview(String sessionId, String json) {
    try (PreparedStatement ps =
        db.connection().prepareStatement("UPDATE sessions SET feedback = ? WHERE id = ?")) {
      ps.setString(1, json);
      ps.setString(2, sessionId);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new IllegalStateException("Cannot store the review", e);
    }
  }
}
