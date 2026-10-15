# Longest Repeating Character Replacement

A string `s` is made of uppercase letters. You may change at most `k` of its characters, each to any uppercase letter you like.

Implement `Relabel.longestRun(String s, int k)` in `Relabel.java`. Return the length of the longest stretch of one repeated letter that you can end up with.

## Examples

- `s = "MNMNNM"`, `k = 2` returns `5`: change the two `M` characters in the first five positions to `N`, giving `NNNNN`.
- `s = "ABCD"`, `k = 0` returns `1`: nothing may change, and no letter appears twice in a row.
- `s = "PPQ"`, `k = 5` returns `3`: there are changes to spare, but the run cannot be longer than the string.

## Constraints

- 0 <= `s.length()` <= 100,000
- 0 <= `k` <= 100,000
- `s` contains only the letters `A` to `Z`
