# Edit Distance

A spell checker measures how far a typed word is from a dictionary word by the fewest fixes that turn one into the other. A fix changes one letter: it inserts a letter anywhere, deletes a letter, or replaces a letter with a different one.

Implement `Typo.fewestFixes(String typed, String word)` in `Typo.java` and return the fewest fixes that turn `typed` into `word`. Either string may be empty.

## Example 1

**Input:** `typed = "plaster"`, `word = "pastel"`
**Output:** `2`
**Explanation:** Delete the l and replace the r with an l: `plaster`, `paster`, `pastel`. One fix is not enough: it would have to be a deletion, since `typed` is one letter longer, and no single letter of `plaster` leaves `pastel` when it is dropped.

## Example 2

**Input:** `typed = "sprint"`, `word = "paints"`
**Output:** `3`
**Explanation:** Delete the s, replace the r with an a and add an s at the end: `sprint`, `print`, `paint`, `paints`. Two fixes are not enough: two replacements would leave four positions as they were, but the words differ at every position, and a deletion with an insertion would keep five letters in order, but the most they share in order are the four of `pint`.

## Example 3

**Input:** `typed = ""`, `word = "fern"`
**Output:** `4`
**Explanation:** Nothing was typed, so each of the four letters has to be inserted.

## Constraints

- `0 <= typed.length() <= 500`
- `0 <= word.length() <= 500`
- `typed` and `word` contain only lowercase English letters.

## Follow-up

What if you only had to know whether the two words are at most k fixes apart, for a small k?
