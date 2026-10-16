# Longest Repeating Character Replacement

A string `s` is made of uppercase letters. You may change at most `k` of its characters, each to any uppercase letter you like.

Implement `Relabel.longestRun(String s, int k)` in `Relabel.java`. Return the length of the longest stretch of one repeated letter that you can end up with.

## Example 1

**Input:** `s = "MNMNNM"`, `k = 2`
**Output:** `5`
**Explanation:** Change the two `M` characters in the first five positions to `N`, giving `NNNNN`. A stretch of six would need three changes.

## Example 2

**Input:** `s = "ABCD"`, `k = 0`
**Output:** `1`
**Explanation:** Nothing may change, and no letter appears twice in a row.

## Example 3

**Input:** `s = "PPQ"`, `k = 5`
**Output:** `3`
**Explanation:** There are changes to spare, but the run cannot be longer than the string.

## Constraints

- `0 <= s.length() <= 100000`
- `0 <= k <= 100000`
- `s` contains only the letters `A` to `Z`.

## Follow-up

Can you do constant work per character, without scanning the 26 letter counts each time?
