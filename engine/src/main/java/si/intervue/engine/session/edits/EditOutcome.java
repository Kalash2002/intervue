package si.intervue.engine.session.edits;

/**
 * One edit after the applier saw it. {@code id} is {@code replyId:editId}, unique in the session;
 * hashes are null unless the edit was applied ({@code beforeHash} is also null for a create).
 */
public record EditOutcome(
    String id,
    String replyId,
    String path,
    String op,
    EditStatus status,
    String message,
    int hunks,
    String beforeHash,
    String afterHash) {}
