# Minimum Window Substring

You are given a `text` and a `need` string. A stretch of the text is a run of consecutive characters. A stretch covers `need` if it contains every character of `need` at least as many times as `need` has it, in any order and possibly with other characters in between.

Implement `Cover.smallest(String text, String need)` in `Cover.java`. Return the shortest stretch of `text` that covers `need`. If several stretches have that shortest length, return the leftmost. If no stretch covers `need`, return the empty string. Uppercase and lowercase letters are different characters.

## Examples

- `text = "xaybzcaby"`, `need = "abc"` returns `"cab"`: it holds one each of `a`, `b` and `c`, and no stretch of two characters can.
- `text = "cabbab"`, `need = "aab"` returns `"abba"`: two `a` characters and a `b` are required, so `"ab"` is not enough.
- `text = "hello"`, `need = "z"` returns `""`: there is no `z` in the text.

## Constraints

- 0 <= `text.length()` <= 100,000
- 1 <= `need.length()` <= 100,000
- Both strings contain only English letters, uppercase and lowercase
