# Longest Substring Without Repeating Characters

Given a string `s`, find the longest stretch of consecutive characters in which no character appears more than once.

Implement `Streaks.longestDistinct(String s)` in `Streaks.java`. Return the length of that stretch. The string may contain any characters, including digits, symbols and letters from any script; uppercase and lowercase letters are different characters.

## Example 1

**Input:** `s = "kayakxyz"`
**Output:** `5`
**Explanation:** The stretch `"akxyz"` has five different characters, and no longer stretch avoids a repeat.

## Example 2

**Input:** `s = "mmm"`
**Output:** `1`
**Explanation:** Every stretch of two or more characters repeats `m`, so the best is a single character.

## Example 3

**Input:** `s = ""`
**Output:** `0`
**Explanation:** There are no characters at all.

## Constraints

- `0 <= s.length() <= 100000`
- Each character of `s` is any Java `char` value.

## Follow-up

Can you do it in O(n) time using a fixed amount of extra memory, however long `s` is?
