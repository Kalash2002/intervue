package si.intervue.engine.session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import si.intervue.engine.session.Sessions.Context;
import si.intervue.engine.session.edits.EditApplier;
import si.intervue.engine.session.edits.EditOutcome;
import si.intervue.engine.session.edits.EditReply;
import si.intervue.engine.session.edits.EditStatus;
import si.intervue.engine.session.edits.EditStore;
import si.intervue.engine.session.edits.UndoResult;

/**
 * The Write code channel's effects on a session: apply a reply's edits, undo them, and reset files
 * (which retires the edits they overwrite). The applier enforces the rules; this records the
 * timeline events every applied edit and undo must leave (PLAN.md 2.8).
 */
final class SessionEdits {

  private final Sessions sessions;
  private final EditStore edits;

  SessionEdits(Sessions sessions, EditStore edits) {
    this.sessions = sessions;
    this.edits = edits;
  }

  EditReply apply(Context c, List<String> lines) {
    EditReply reply;
    try {
      reply = applier(c).apply(lines);
    } catch (IllegalArgumentException e) {
      throw new SessionException(e.getMessage());
    }
    for (EditOutcome o : reply.outcomes()) {
      if (o.status() == EditStatus.APPLIED) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", o.id());
        data.put("path", o.path());
        data.put("op", o.op());
        data.put("hunks", o.hunks());
        data.put("beforeHash", o.beforeHash());
        data.put("afterHash", o.afterHash());
        data.put("replyId", o.replyId());
        sessions.event(c.session(), "aiEdit", data);
      }
    }
    return reply;
  }

  UndoResult undo(Context c, String editId) {
    UndoResult result = applier(c).undo(editId, sessions.now());
    recordUndo(c, result);
    return result;
  }

  List<UndoResult> undoReply(Context c, String replyId) {
    List<UndoResult> results = applier(c).undoReply(replyId, sessions.now());
    results.forEach(r -> recordUndo(c, r));
    return results;
  }

  String resetFile(Context c, String path) {
    String content = c.work().resetFile(path);
    edits.retireForPath(c.session().id(), path, sessions.now());
    sessions.event(c.session(), "resetFile", Map.of("path", path));
    return content;
  }

  void resetProblem(Context c) {
    c.work().resetProblem();
    edits.retireAll(c.session().id(), sessions.now());
    sessions.event(c.session(), "resetProblem", Map.of());
  }

  private void recordUndo(Context c, UndoResult result) {
    if (result.undone()) {
      sessions.event(c.session(), "aiEditUndo", Map.of("id", result.id()));
    }
  }

  private EditApplier applier(Context c) {
    return new EditApplier(
        c.session().id(), c.work(), c.work().root().getParent().resolve("edits"), edits);
  }
}
