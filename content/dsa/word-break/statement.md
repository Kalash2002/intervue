# Word Break

A message was typed with the space bar broken, so its words run together. You have the message `text` and the list `words` of the words it may use. Decide whether spaces can be put back so that every piece between them is one of those words. A word may be used any number of times, words that are not needed are left unused, and every letter must end up in exactly one piece.

Implement `Segmenter.canSplit(String text, List<String> words)` in `Segmenter.java` and return `true` when such a split exists.

## Example 1

**Input:** `text = "seatide"`, `words = ["seat", "sea", "tide"]`
**Output:** `true`
**Explanation:** `sea` + `tide`. Starting with the longer `seat` leaves `ide`, which no word fits, so the first piece has to be chosen with the rest in mind.

## Example 2

**Input:** `text = "coconut"`, `words = ["co", "nut"]`
**Output:** `true`
**Explanation:** `co` + `co` + `nut`: the same word can be used twice.

## Example 3

**Input:** `text = "plumbs"`, `words = ["plum", "plumb", "sum"]`
**Output:** `false`
**Explanation:** The message can only start with `plum` or `plumb`, which leave `bs` and `s`, and no word fits either.

## Example 4

**Input:** `text = "ox"`, `words = ["oxen"]`
**Output:** `false`
**Explanation:** The only word is longer than the whole message.

## Constraints

- `1 <= text.length() <= 300`
- `1 <= words.size() <= 1000`
- `1 <= words.get(i).length() <= 20`
- `text` and the words hold only lowercase English letters.
- All words are different.

## Follow-up

What if you had to list every way to split the message?
