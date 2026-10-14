package si.intervue.engine.session.edits;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import si.intervue.engine.store.Database;

/** SQL for the {@code edits} table: one row per applied AI edit, retired by undo or reset. */
public final class EditStore {

  /** One applied edit; {@code snapshotPath} and {@code beforeHash} are null for a create. */
  record EditRecord(
      String id,
      String replyId,
      String path,
      String op,
      String beforeHash,
      String afterHash,
      String snapshotPath,
      Long undoneAt) {}

  private final Database db;

  public EditStore(Database db) {
    this.db = db;
  }

  void insert(String sessionId, EditRecord e) {
    update(
        "INSERT INTO edits(session_id, edit_id, reply_id, path, op, before_hash, after_hash, snapshot_path)"
            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
        sessionId,
        e.id(),
        e.replyId(),
        e.path(),
        e.op(),
        e.beforeHash(),
        e.afterHash(),
        e.snapshotPath());
  }

  boolean exists(String sessionId, String id) {
    return find(sessionId, id).isPresent();
  }

  Optional<EditRecord> find(String sessionId, String id) {
    List<EditRecord> rows =
        query("SELECT * FROM edits WHERE session_id = ? AND edit_id = ?", sessionId, id);
    return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
  }

  List<EditRecord> activeInReply(String sessionId, String replyId) {
    return query(
        "SELECT * FROM edits WHERE session_id = ? AND reply_id = ? AND undone_at IS NULL ORDER BY rowid DESC",
        sessionId,
        replyId);
  }

  void markUndone(String sessionId, String id, long at) {
    update(
        "UPDATE edits SET undone_at = ? WHERE session_id = ? AND edit_id = ?", at, sessionId, id);
  }

  /** A reset overwrote these edits; they can no longer be undone. */
  public void retireForPath(String sessionId, String path, long at) {
    update(
        "UPDATE edits SET undone_at = ? WHERE session_id = ? AND path = ? AND undone_at IS NULL",
        at,
        sessionId,
        path);
  }

  public void retireAll(String sessionId, long at) {
    update(
        "UPDATE edits SET undone_at = ? WHERE session_id = ? AND undone_at IS NULL", at, sessionId);
  }

  private List<EditRecord> query(String sql, Object... args) {
    List<EditRecord> rows = new ArrayList<>();
    try (PreparedStatement ps = db.connection().prepareStatement(sql)) {
      for (int i = 0; i < args.length; i++) {
        ps.setObject(i + 1, args[i]);
      }
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          // wasNull() reports on the last column read, so it must follow getLong directly.
          long undone = rs.getLong("undone_at");
          Long undoneAt = rs.wasNull() ? null : undone;
          rows.add(
              new EditRecord(
                  rs.getString("edit_id"),
                  rs.getString("reply_id"),
                  rs.getString("path"),
                  rs.getString("op"),
                  rs.getString("before_hash"),
                  rs.getString("after_hash"),
                  rs.getString("snapshot_path"),
                  undoneAt));
        }
      }
    } catch (SQLException e) {
      throw new IllegalStateException("Cannot read edits", e);
    }
    return rows;
  }

  private void update(String sql, Object... args) {
    try (PreparedStatement ps = db.connection().prepareStatement(sql)) {
      for (int i = 0; i < args.length; i++) {
        ps.setObject(i + 1, args[i]);
      }
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new IllegalStateException("Cannot write edits", e);
    }
  }
}
