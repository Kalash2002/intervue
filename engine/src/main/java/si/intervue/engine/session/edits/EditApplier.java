package si.intervue.engine.session.edits;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.regex.Pattern;
import si.intervue.engine.session.edits.EditProtocol.Line;
import si.intervue.engine.session.edits.EditStore.EditRecord;

/**
 * The only code that writes AI edits (docs/contracts/edit-protocol.md): it checks every rule,
 * snapshots before writing, records what it applied and undoes it on request. It writes no timeline
 * events; the caller records them from the outcomes.
 */
public final class EditApplier {

  static final int MAX_BYTES = 200 * 1024;
  private static final Pattern ID = Pattern.compile("[A-Za-z0-9_-]{1,40}");
  private static final String CHANGED =
      "your file changed; the suggestion is shown but not applied";

  private final String sessionId;
  private final EditableFiles files;
  private final Path snapshots;
  private final EditStore store;

  public EditApplier(String sessionId, EditableFiles files, Path snapshots, EditStore store) {
    this.sessionId = sessionId;
    this.files = files;
    this.snapshots = snapshots;
    this.store = store;
  }

  /** Parses and applies a whole reply, edit by edit; throws only when the stream is unreadable. */
  public EditReply apply(List<String> lines) {
    EditProtocol.Stream stream = EditProtocol.parse(lines);
    String replyId = stream.replyId();
    if (replyId == null || !ID.matcher(replyId).matches()) {
      throw new IllegalArgumentException("The assistant's reply has no usable reply id.");
    }
    List<EditOutcome> outcomes = new ArrayList<>();
    for (Line edit : stream.edits()) {
      outcomes.add(applyOne(replyId, edit));
    }
    return new EditReply(replyId, stream.note(), outcomes);
  }

  private EditOutcome applyOne(String replyId, Line e) {
    if (e.id() == null || !ID.matcher(e.id()).matches()) {
      return refused(replyId, e, "the edit has no usable id");
    }
    String id = replyId + ":" + e.id();
    if (store.exists(sessionId, id)) {
      return refused(replyId, e, "an edit with this id was already applied");
    }
    Path file;
    try {
      file = files.resolve(e.path());
    } catch (RuntimeException outside) {
      return refused(replyId, e, "not allowed: " + e.path());
    }
    String extension = e.path().contains(".") ? e.path().substring(e.path().lastIndexOf('.')) : "";
    if (!files.isEditable(e.path()) || !files.sourceExtensions().contains(extension)) {
      return refused(replyId, e, "not allowed: " + e.path() + " is not an editable source file");
    }
    return switch (e.op() == null ? "" : e.op()) {
      case "replace" -> replace(replyId, id, e, file);
      case "create" -> create(replyId, id, e, file);
      default -> refused(replyId, e, "unknown operation " + e.op());
    };
  }

  private EditOutcome replace(String replyId, String id, Line e, Path file) {
    if (!Files.isRegularFile(file)) {
      return refused(replyId, e, "not allowed: " + e.path() + " does not exist");
    }
    byte[] bytes = readBytes(file);
    String text = utf8(bytes);
    if (bytes.length > MAX_BYTES || text == null) {
      return refused(replyId, e, "not allowed: files over 200 KB or not in UTF-8 are not edited");
    }
    String beforeHash = hash(bytes);
    if (!beforeHash.equals(e.base())) {
      return outcome(replyId, e, EditStatus.SUGGESTION, CHANGED, null, null);
    }
    // Inserted text follows the file's own line endings, so a CRLF file stays CRLF.
    boolean crlf = text.contains("\r\n");
    String search = endings(e.search(), crlf);
    String replacement = endings(e.replace(), crlf);
    if (search.isEmpty()
        || text.indexOf(search) < 0
        || text.indexOf(search) != text.lastIndexOf(search)) {
      return outcome(
          replyId,
          e,
          EditStatus.SUGGESTION,
          "the text to replace was not found exactly once",
          null,
          null);
    }
    String after = text.replace(search, replacement);
    Path snapshot = snapshots.resolve(replyId + "-" + e.id() + ".before");
    copy(file, snapshot);
    try {
      Files.writeString(file, after, StandardCharsets.UTF_8);
    } catch (IOException failed) {
      copy(snapshot, file);
      return refused(replyId, e, "the file could not be written; it is unchanged");
    }
    String afterHash = hash(after.getBytes(StandardCharsets.UTF_8));
    store.insert(
        sessionId,
        new EditRecord(
            id, replyId, e.path(), "replace", beforeHash, afterHash, snapshot.toString(), null));
    return outcome(replyId, e, EditStatus.APPLIED, "applied", beforeHash, afterHash);
  }

  private EditOutcome create(String replyId, String id, Line e, Path file) {
    String content = e.content() == null ? "" : e.content();
    byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
    if (Files.exists(file)) {
      return refused(replyId, e, "not allowed: " + e.path() + " already exists");
    }
    if (bytes.length > MAX_BYTES) {
      return refused(replyId, e, "not allowed: new files over 200 KB are not created");
    }
    try {
      Files.createDirectories(file.getParent());
      Files.write(file, bytes);
    } catch (IOException failed) {
      return refused(replyId, e, "the file could not be created");
    }
    String afterHash = hash(bytes);
    store.insert(
        sessionId, new EditRecord(id, replyId, e.path(), "create", null, afterHash, null, null));
    return outcome(replyId, e, EditStatus.APPLIED, "applied", null, afterHash);
  }

  /** Undoes one edit if the file still holds exactly what the edit wrote; otherwise only a diff. */
  public UndoResult undo(String id, long at) {
    EditRecord edit = store.find(sessionId, id).orElse(null);
    if (edit == null || edit.undoneAt() != null) {
      return new UndoResult(id, false, "");
    }
    Path file = files.resolve(edit.path());
    String current = Files.exists(file) ? new String(readBytes(file), StandardCharsets.UTF_8) : "";
    boolean unchanged = Files.exists(file) && hash(readBytes(file)).equals(edit.afterHash());
    String before =
        edit.snapshotPath() == null
            ? ""
            : new String(readBytes(Path.of(edit.snapshotPath())), StandardCharsets.UTF_8);
    if (!unchanged) {
      return new UndoResult(id, false, LineDiff.of(current, before));
    }
    if (edit.op().equals("create")) {
      delete(file);
    } else {
      copy(Path.of(edit.snapshotPath()), file);
    }
    store.markUndone(sessionId, id, at);
    return new UndoResult(id, true, "");
  }

  /** "Undo everything from this reply": newest first, so stacked edits unwind in order. */
  public List<UndoResult> undoReply(String replyId, long at) {
    List<UndoResult> results = new ArrayList<>();
    for (EditRecord edit : store.activeInReply(sessionId, replyId)) {
      results.add(undo(edit.id(), at));
    }
    return results;
  }

  private static EditOutcome outcome(
      String replyId, Line e, EditStatus status, String message, String before, String after) {
    String id = e.id() == null ? null : replyId + ":" + e.id();
    return new EditOutcome(id, replyId, e.path(), e.op(), status, message, 1, before, after);
  }

  private static EditOutcome refused(String replyId, Line e, String message) {
    return outcome(replyId, e, EditStatus.NOT_ALLOWED, message, null, null);
  }

  private static String endings(String text, boolean crlf) {
    String lf = text == null ? "" : text.replace("\r\n", "\n");
    return crlf ? lf.replace("\n", "\r\n") : lf;
  }

  /** The text, or null when the bytes are not valid UTF-8. */
  private static String utf8(byte[] bytes) {
    try {
      return StandardCharsets.UTF_8
          .newDecoder()
          .onMalformedInput(CodingErrorAction.REPORT)
          .onUnmappableCharacter(CodingErrorAction.REPORT)
          .decode(ByteBuffer.wrap(bytes))
          .toString();
    } catch (CharacterCodingException e) {
      return null;
    }
  }

  /** The base hash the edit protocol carries: {@code sha256:} and the hex digest of the bytes. */
  public static String hash(byte[] bytes) {
    try {
      return "sha256:"
          + HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 is part of every JDK", e);
    }
  }

  private static byte[] readBytes(Path file) {
    try {
      return Files.readAllBytes(file);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static void copy(Path from, Path to) {
    try {
      Files.createDirectories(to.getParent());
      Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static void delete(Path file) {
    try {
      Files.deleteIfExists(file);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
