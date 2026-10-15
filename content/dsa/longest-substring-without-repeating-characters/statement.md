# Longest Substring Without Repeating Characters

Given a string `s`, find the longest stretch of consecutive characters in which no character appears more than once.

Implement `Streaks.longestDistinct(String s)` in `Streaks.java`. Return the length of that stretch. The string may contain any characters, including digits, symbols and letters from any script; uppercase and lowercase letters are different characters.

## Examples

- `s = "kayakxyz"` returns `5`: the stretch `"akxyz"` has five different characters, and no longer stretch avoids a repeat.
- `s = "mmm"` returns `1`: every stretch of two or more characters repeats `m`.
- `s = ""` returns `0`: there are no characters at all.

## Constraints

- 0 <= `s.length()` <= 100,000
- Each character is any Java `char` value
