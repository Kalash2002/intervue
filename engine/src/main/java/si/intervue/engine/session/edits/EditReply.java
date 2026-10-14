package si.intervue.engine.session.edits;

import java.util.List;

/** One Write code reply: its note for the chat and what happened to each edit, in order. */
public record EditReply(String replyId, String note, List<EditOutcome> outcomes) {}
