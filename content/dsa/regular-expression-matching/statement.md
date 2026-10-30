# Regular Expression Matching

A search tool takes patterns in a small language. A pattern is a string of lowercase letters, dots and stars, read from left to right as a list of pieces:

- a letter on its own is a piece that matches exactly that letter;
- a dot `.` on its own is a piece that matches any one letter;
- a letter or a dot followed by a star `*` is one piece, and it matches any number of letters in a row, none at all included, each of which the letter or the dot would match on its own. So `e*` matches the empty string, `e`, `ee` and so on, and `.*` matches any run of letters, since each copy of the dot can be a different letter.

A star always comes straight after a letter or a dot: a pattern never starts with a star and never has two stars in a row.

Implement `Stencil.matches(String text, String pattern)` in `Stencil.java` and return `true` if the pieces of `pattern`, in order, can match consecutive parts of `text` that together make up all of it. Matching only a beginning, an end or a middle part of `text` does not count. Return `false` otherwise. Either string may be empty; an empty pattern matches only the empty text.

## Example 1

**Input:** `text = "toast"`, `pattern = "to.a*st"`
**Output:** `true`
**Explanation:** The dot takes the a, so the piece `a*` comes to the s and matches no a at all; the t, the o, the s and the last t match one for one.

## Example 2

**Input:** `text = "seventeen"`, `pattern = "s.*en"`
**Output:** `true`
**Explanation:** The `.*` takes `evente`, leaving the last `en` for the end of the pattern. Stopping it at the first `en`, after `seven`, would leave `teen` unmatched, and letting it run to the end would leave nothing for `en`.

## Example 3

**Input:** `text = "banana"`, `pattern = "ban*"`
**Output:** `false`
**Explanation:** The star repeats only the n, so the pattern matches `ba`, `ban`, `bann` and so on, and never a text that goes on with an a after the n.

## Example 4

**Input:** `text = ""`, `pattern = "x*y*"`
**Output:** `true`
**Explanation:** Both pieces match no letters at all, which is all the empty text needs.

## Constraints

- `0 <= text.length() <= 100`
- `0 <= pattern.length() <= 100`
- `text` contains only lowercase English letters.
- `pattern` contains only lowercase English letters, `.` and `*`.
- Every `*` in `pattern` comes straight after a letter or a `.`.

## Follow-up

What if the text arrived one letter at a time and was too long to keep in memory?
