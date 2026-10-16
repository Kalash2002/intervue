# Longest Valid Parentheses

A build log is reduced to a string `s` made only of the characters `(` and `)`. A stretch of consecutive characters is balanced when every `(` in it is closed by a later `)` in the same stretch, every `)` closes an earlier `(` in it, and the pairs nest without crossing. So `(())()` is balanced, while `)(` and `(()` are not.

Implement `Brackets.longestBalanced(String s)` in `Brackets.java`. Return the length of the longest balanced stretch of consecutive characters of `s`, or `0` if there is none, including for the empty string. A stretch must be unbroken: characters picked from scattered places do not count.

## Example 1

**Input:** `s = "()(()))()"`
**Output:** `6`
**Explanation:** The first six characters, `()(())`, are balanced. The `)` at position 6 has nothing left to close, so it ends that stretch, and the `()` after it is only 2 long.

## Example 2

**Input:** `s = "))(())("`
**Output:** `4`
**Explanation:** The stretch `(())` at positions 2 to 5 is balanced. The two `)` in front and the `(` at the end have no partner.

## Example 3

**Input:** `s = "((("`
**Output:** `0`
**Explanation:** Nothing is ever closed, so no stretch is balanced.

## Constraints

- `0 <= s.length() <= 10^5`
- `s[i]` is `(` or `)`

## Follow-up

Can you do it in constant extra space?
