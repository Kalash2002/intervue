package si.intervue.engine.session.edits;

import java.util.ArrayList;
import java.util.List;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * Parser for edit protocol v1 (docs/contracts/edit-protocol.md): a start line with a known version,
 * then notes and edits. Unknown line types and fields are ignored.
 */
public final class EditProtocol {

  public static final int PROTOCOL = 1;

  private static final ObjectMapper JSON =
      JsonMapper.builder().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();

  /** Every field any line type may carry. */
  record Line(
      String t,
      Integer protocol,
      String replyId,
      String text,
      String id,
      String path,
      String op,
      String base,
      String search,
      String replace,
      String content) {}

  /** A parsed stream: the reply id, the joined notes, and the edit lines in order. */
  record Stream(String replyId, String note, List<Line> edits) {}

  private EditProtocol() {}

  static Stream parse(List<String> rawLines) {
    List<Line> lines = new ArrayList<>();
    for (String raw : rawLines) {
      if (raw.isBlank()) {
        continue;
      }
      try {
        lines.add(JSON.readValue(raw, Line.class));
      } catch (JacksonException e) {
        throw new IllegalArgumentException(
            "The assistant's reply is not valid edit protocol JSON.", e);
      }
    }
    if (lines.isEmpty() || !"start".equals(lines.get(0).t()) || lines.get(0).protocol() == null) {
      throw new IllegalArgumentException("The assistant's reply does not start with a start line.");
    }
    if (lines.get(0).protocol() != PROTOCOL) {
      throw new IllegalArgumentException(
          "Edit protocol " + lines.get(0).protocol() + " is not supported.");
    }
    StringBuilder note = new StringBuilder();
    List<Line> edits = new ArrayList<>();
    for (Line line : lines.subList(1, lines.size())) {
      if ("note".equals(line.t()) && line.text() != null) {
        note.append(note.isEmpty() ? "" : "\n").append(line.text());
      } else if ("edit".equals(line.t())) {
        edits.add(line);
      }
    }
    return new Stream(lines.get(0).replyId(), note.toString(), edits);
  }
}
