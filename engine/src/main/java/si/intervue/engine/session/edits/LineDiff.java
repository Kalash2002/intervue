package si.intervue.engine.session.edits;

import java.util.List;

/**
 * A one-hunk line diff: the common start and end are skipped and the differing middle is shown,
 * {@code -} for the user's current lines and {@code +} for the lines before the edit. Enough to let
 * the user decide on an undo; not a general diff tool.
 */
final class LineDiff {

  private LineDiff() {}

  static String of(String current, String before) {
    List<String> a = current.lines().toList();
    List<String> b = before.lines().toList();
    int start = 0;
    while (start < a.size() && start < b.size() && a.get(start).equals(b.get(start))) {
      start++;
    }
    int endA = a.size();
    int endB = b.size();
    while (endA > start && endB > start && a.get(endA - 1).equals(b.get(endB - 1))) {
      endA--;
      endB--;
    }
    StringBuilder diff = new StringBuilder("--- your file\n+++ before the assistant's edit\n");
    diff.append("@@ line ").append(start + 1).append(" @@\n");
    a.subList(start, endA).forEach(line -> diff.append('-').append(line).append('\n'));
    b.subList(start, endB).forEach(line -> diff.append('+').append(line).append('\n'));
    return diff.toString();
  }
}
