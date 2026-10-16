# Valid Palindrome

A phrase is a palindrome when it reads the same forwards and backwards, once you ignore upper and lower case and drop every character that is not a letter or a digit.

Implement `Palindromes.isPalindrome(String s)` in `Palindromes.java`. Return `true` when `s` is a palindrome by that rule and `false` otherwise. A string with no letters or digits at all counts as a palindrome.

## Example 1

**Input:** `s = "Mo, 12 a 21 oM!"`
**Output:** `true`
**Explanation:** Ignoring case, spaces and punctuation, the text is `mo12a21om`, which reads the same in both directions.

## Example 2

**Input:** `s = "Kayak, 7!"`
**Output:** `false`
**Explanation:** The text is `kayak7`. Digits are not ignored, so the `7` at the end has to match the `k` at the start, and it does not.

## Example 3

**Input:** `s = " .,; "`
**Output:** `true`
**Explanation:** Every character is ignored, so nothing is left to compare.

## Constraints

- `0 <= s.length() <= 500000`
- `s` contains only printable ASCII characters (space to `~`).
