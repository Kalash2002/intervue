# Palindromic Substrings

You are given a string `s` of lowercase letters. A substring is a run of neighbouring letters of `s`, and it is a palindrome when it reads the same from right to left as from left to right.

Implement `PalindromeTally.count(String s)` in `PalindromeTally.java` and return how many substrings of `s` are palindromes. Substrings at different positions count separately, even when they hold the same letters, and every single letter counts.

## Example 1

**Input:** `s = "tatty"`
**Output:** `7`
**Explanation:** The five single letters, plus `tat` and `tt`. No longer stretch reads the same both ways.

## Example 2

**Input:** `s = "wowow"`
**Output:** `9`
**Explanation:** Five single letters, `wow` starting at position 0 and again at position 2 (two substrings), `owo`, and the whole string.

## Example 3

**Input:** `s = "q"`
**Output:** `1`
**Explanation:** A single letter is a palindrome, and it is the only substring.

## Constraints

- `1 <= s.length() <= 5000`
- `s` holds only lowercase English letters.

## Follow-up

Can you count them in O(n) time?
