# Minimum Window Substring

You are given two strings, `text` and `need`. A stretch of the text is a run of consecutive characters. A stretch covers `need` when it holds every character of `need` at least as many times as `need` has it, in any order and possibly with other characters in between.

Implement `Cover.smallest(String text, String need)` in `Cover.java`. Return the shortest stretch of `text` that covers `need`. If several stretches share that shortest length, return the leftmost one. If no stretch covers `need`, return the empty string. Uppercase and lowercase letters are different characters.

## Example 1

**Input:** `text = "xaybzcaby"`, `need = "abc"`
**Output:** `"cab"`
**Explanation:** `"cab"` holds one each of `a`, `b` and `c`, and no stretch of two characters can hold three letters.

## Example 2

**Input:** `text = "cabbab"`, `need = "aab"`
**Output:** `"abba"`
**Explanation:** Two `a` characters and a `b` are required, so `"ab"` is not enough, and no stretch of three characters has two `a` characters.

## Example 3

**Input:** `text = "hello"`, `need = "lll"`
**Output:** `""`
**Explanation:** The text has only two `l` characters, so no stretch can hold three.

## Constraints

- `0 <= text.length <= 10^5`
- `1 <= need.length <= 10^5`
- `text` and `need` consist of English letters, uppercase and lowercase.

## Follow-up

Can you do it in O(`text.length` + `need.length`) time?
