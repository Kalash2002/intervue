package si.intervue.engine.session;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import si.intervue.engine.ai.AiRequest;
import si.intervue.engine.sections.SessionType;
import si.intervue.engine.session.edits.EditApplier;
import si.intervue.problemformat.Variant;

/**
 * Builds model calls in the order of PLAN.md 2.5 from the section's prompt files, read at call
 * time: the rules (system, mode, channel) become the system prompt; the brief, read-only files,
 * editable files with their base hashes, history and message become the user turn. Prompt text
 * lives in {@code sections/<id>/prompts}, never here.
 */
final class PromptBuilder {

  /** Interview rules are point-and-explain (PLAN.md 2.8); Helpful is Practice's default. */
  enum Rules {
    INTERVIEW,
    HELPFUL
  }

  /** One line of history at an active-time moment: a chat turn or a past edit. */
  record Entry(long tMs, String line) {
    static Entry turn(String role, String text, long tMs) {
      return new Entry(tMs, (role.equals("user") ? "Candidate: " : "Assistant: ") + text);
    }

    /** Past edits are summarised to one line each (PLAN.md 2.5). */
    static Entry edit(StoredEvent e) {
      String id = String.valueOf(e.data().get("id"));
      return new Entry(
          e.tMs(),
          e.type().equals("aiEditUndo")
              ? "(AI edit " + id + " undone)"
              : "(AI edit " + id + " applied to " + e.data().get("path") + ")");
    }
  }

  private static final int MAX_FILE_CHARS = 100_000;

  private PromptBuilder() {}

  static AiRequest ask(Sessions.Context c, Rules rules, List<Entry> history, String message) {
    return new AiRequest(rules(c, rules), userTurn(c, history, message));
  }

  static AiRequest write(
      Sessions.Context c, Rules rules, List<Entry> history, String message, String replyId) {
    String channel =
        fill(
            c.section().prompts().template("write-code"),
            Map.of("replyId", replyId, "extension", extension(c)));
    return new AiRequest(rules(c, rules) + "\n\n" + channel, userTurn(c, history, message));
  }

  /** Hint explanations use the Ask rules and carry only the level the candidate revealed. */
  static AiRequest hint(
      Sessions.Context c, List<Entry> history, int phase, int level, String text) {
    String channel =
        fill(
            c.section().prompts().template("hint-explain"),
            Map.of(
                "phase", String.valueOf(phase), "level", String.valueOf(level), "hintText", text));
    return new AiRequest(
        rules(c, Rules.HELPFUL) + "\n\n" + channel,
        userTurn(c, history, "Explain the revealed hint further."));
  }

  /** The written review: the grader notes, the whole timeline and the chat are its evidence. */
  static AiRequest feedback(Sessions.Context c, List<Entry> history, List<StoredEvent> events) {
    String label = c.session().type() == SessionType.INTERVIEW ? "Interview" : "Practice";
    String rules =
        fill(
            c.section().prompts().template("feedback"),
            Map.of("minutes", String.valueOf(c.session().minutes()), "sessionLabel", label));
    StringBuilder p = new StringBuilder();
    section(p, "Grader notes", Sessions.readString(c.problem().dir().resolve("grading/NOTES.md")));
    section(p, "Follow-up questions", String.join("\n", c.problem().problem().followUps()));
    StringBuilder timeline = new StringBuilder();
    for (StoredEvent e : events) {
      timeline.append(clock(e.tMs())).append(' ').append(e.type()).append(' ').append(e.data());
      timeline.append('\n');
    }
    section(p, "Timeline", timeline.toString());
    String finals = c.session().finalResultsJson();
    section(p, "Final test results", finals == null ? "Not finished." : finals);
    section(p, "Candidate notes", c.session().notes());
    p.append(userTurn(c, history, "Write the review."));
    return new AiRequest(rules, p.toString());
  }

  private static String rules(Sessions.Context c, Rules rules) {
    Variant variant = c.problem().variants().get(c.session().language());
    String system =
        fill(
            c.section().prompts().template("system"),
            Map.of(
                "language", c.language().definition().displayName(),
                "toolchainVersion", variant.minToolchain()));
    String mode = rules == Rules.INTERVIEW ? "interview-mode" : "helpful-mode";
    return system.strip() + "\n\n" + c.section().prompts().template(mode).strip();
  }

  private static String userTurn(Sessions.Context c, List<Entry> history, String message) {
    StringBuilder p = new StringBuilder();
    StringBuilder brief = new StringBuilder();
    Sessions.brief(c).stream().filter(b -> b != null).forEach(b -> brief.append(b).append("\n\n"));
    section(p, "Brief", brief.toString());
    StringBuilder readOnly = new StringBuilder();
    StringBuilder editable = new StringBuilder();
    String fence = c.language().definition().id();
    for (FileEntry f : c.work().list()) {
      byte[] bytes = c.work().readBytes(f.path());
      String text = new String(bytes, StandardCharsets.UTF_8);
      if (text.length() > MAX_FILE_CHARS) {
        text = text.substring(0, MAX_FILE_CHARS) + "\n... (cut: file too long)";
      }
      boolean canEdit = c.work().isEditable(f.path());
      StringBuilder into = canEdit ? editable : readOnly;
      into.append("## ").append(f.path());
      if (canEdit) {
        into.append(" (base ").append(EditApplier.hash(bytes)).append(')');
      }
      into.append("\n```").append(fence).append('\n').append(text).append("\n```\n\n");
    }
    section(p, "Read-only files (do not edit)", readOnly.toString());
    section(p, "Editable files", editable.toString());
    List<Entry> ordered = new ArrayList<>(history);
    ordered.sort(Comparator.comparingLong(Entry::tMs));
    StringBuilder h = new StringBuilder();
    ordered.forEach(e -> h.append(e.line()).append('\n'));
    section(p, "Conversation so far", h.isEmpty() ? "(none)" : h.toString());
    section(p, "Message", message);
    return p.toString();
  }

  private static void section(StringBuilder p, String title, String body) {
    p.append("# ").append(title).append("\n\n").append(body.strip()).append("\n\n");
  }

  private static String extension(Sessions.Context c) {
    return c.language().definition().extensions().get(0);
  }

  /** Replaces each {@code {{name}}}; templates name only the keys their channel provides. */
  static String fill(String template, Map<String, String> values) {
    String out = template;
    for (Map.Entry<String, String> v : values.entrySet()) {
      out = out.replace("{{" + v.getKey() + "}}", v.getValue());
    }
    return out;
  }

  private static String clock(long ms) {
    long s = ms / 1000;
    return (s / 60) + ":" + String.format(Locale.ROOT, "%02d", s % 60);
  }
}
