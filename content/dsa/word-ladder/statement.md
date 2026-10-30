# Word Ladder

A word game starts from one word and changes a single letter at a time until it becomes another word. Every word you produce along the way, including the last one, must appear in a given list of allowed words. The starting word does not need to be in the list.

Implement `Ladder.fewestChanges(String begin, String end, List<String> allowed)` in `Ladder.java`. Return the smallest number of single-letter changes that turn `begin` into `end`, or `-1` when it cannot be done. The answer counts changes, not words: a ladder of four words, `begin` included, takes three changes. If `begin` equals `end`, the answer is `0`.

## Example 1

**Input:** `begin = "rope"`, `end = "mule"`, `allowed = ["mile", "mole", "role", "rose", "mule", "pole"]`
**Output:** `3`
**Explanation:** The ladder rope, role, mole, mule changes one letter three times. No ladder is shorter, because `rope` and `mule` differ in three positions.

## Example 2

**Input:** `begin = "tin"`, `end = "tip"`, `allowed = ["tip", "pin", "tan"]`
**Output:** `1`
**Explanation:** One letter changes, and `tip` is allowed.

## Example 3

**Input:** `begin = "sun"`, `end = "sub"`, `allowed = ["sin", "fun"]`
**Output:** `-1`
**Explanation:** `sub` is not an allowed word, so no ladder can end there.

## Example 4

**Input:** `begin = "kit"`, `end = "kit"`, `allowed = []`
**Output:** `0`
**Explanation:** The word is already the target, so no change is needed.

## Constraints

- `1 <= begin.length <= 10`
- `end` and every word in `allowed` have the same length as `begin`.
- Every word uses only lowercase letters `a` to `z`.
- `0 <= allowed.size() <= 50000`
- The words in `allowed` are distinct.

## Follow-up

How would you return the whole sequence of words, not just the number of changes?
