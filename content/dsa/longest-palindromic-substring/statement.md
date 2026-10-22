# Longest Palindromic Substring

You are given a string `s` of letters and digits. A substring is a run of neighbouring characters of `s`, and it is a palindrome when it reads the same from right to left as from left to right. Uppercase and lowercase letters are different characters, so `Aa` is not a palindrome.

Implement `PalindromeSpan.longest(String s)` in `PalindromeSpan.java` and return the longest substring of `s` that is a palindrome. When several palindromes share that longest length, return the one that starts first. A single character is a palindrome, so there is always an answer.

## Example 1

**Input:** `s = "tqpopqm"`
**Output:** `"qpopq"`
**Explanation:** `qpopq` reads the same both ways and contains the shorter palindrome `pop`. The whole string does not, because it starts with `t` and ends with `m`.

## Example 2

**Input:** `s = "Abbazxz"`
**Output:** `"zxz"`
**Explanation:** `Abba` would be a palindrome if case were ignored, but `A` and `a` differ. That leaves `bb` and `zxz`, and `zxz` is longer.

## Example 3

**Input:** `s = "nunzez"`
**Output:** `"nun"`
**Explanation:** `nun` and `zez` both have length 3 and nothing longer reads the same both ways, so the one that starts first is returned.

## Example 4

**Input:** `s = "k3Z"`
**Output:** `"k"`
**Explanation:** No two characters of the string are equal, so the longest palindromes are the single characters, and `k` starts first.

## Constraints

- `1 <= s.length() <= 5000`
- `s` holds only English letters and digits.

## Follow-up

Can you beat O(n²) time?
