# Valid Anagram

Two words are anagrams when the letters of one can be rearranged to spell the other, using every letter exactly as many times as it appears.

Implement `Anagrams.areAnagrams(String a, String b)` in `Anagrams.java`. Return `true` when `a` and `b` are anagrams of each other and `false` otherwise. Two empty strings are anagrams.

## Examples

- `a = "rail"`, `b = "liar"` returns `true`: both use one each of `r`, `a`, `i` and `l`.
- `a = "aab"`, `b = "abb"` returns `false`: the lengths match, but `a` has two `a` and `b` has two `b`.
- `a = "ab"`, `b = "abc"` returns `false`: the second word has a letter the first lacks.

## Constraints

- 0 <= `a.length()`, `b.length()` <= 200,000
- `a` and `b` contain only lowercase letters `a` to `z`.
