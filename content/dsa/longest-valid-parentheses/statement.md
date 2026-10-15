# Longest Valid Parentheses

A build log is reduced to a string made only of the characters `(` and `)`. A stretch of the string is balanced when every `(` in it is closed by a later `)` in the same stretch, every `)` closes an earlier `(` in it, and the pairs nest properly. For example `(())()` is balanced, while `)(` and `(()` are not.

Implement `Brackets.longestBalanced(String s)` in `Brackets.java`. Return the length of the longest balanced stretch made of consecutive characters of `s` (a substring, not a selection of scattered characters). Return `0` if there is none, including for the empty string.

## Examples

- `s = "(()"` returns `2`: the last two characters, `()`, are balanced; the first `(` is never closed.
- `s = ")()())"` returns `4`: the middle `()()` is balanced and the two `)` around it are not.
- `s = "(())()("` returns `6`: everything except the last character.

## Constraints

- 0 <= `s.length()` <= 100,000
- `s` contains only `(` and `)`.
