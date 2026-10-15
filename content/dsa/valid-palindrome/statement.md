# Valid Palindrome

A phrase is a palindrome when it reads the same forwards and backwards, once you ignore upper and lower case and drop everything that is not a letter or a digit.

Implement `Palindromes.isPalindrome(String s)` in `Palindromes.java`. Return `true` when `s` is a palindrome by that rule and `false` otherwise. A string with no letters or digits at all counts as a palindrome.

## Examples

- `s = "Never odd, or even!"` returns `true`: ignoring case and punctuation it reads `neveroddoreven` both ways.
- `s = "a7"` returns `false`: the letter and the digit differ, and digits are not ignored.
- `s = " .,; "` returns `true`: nothing is left to compare.

## Constraints

- 0 <= `s.length()` <= 500,000
- `s` contains only printable ASCII characters (space to `~`).
