# Partition Labels

A string `s` of lowercase letters is to be cut into pieces, as many as possible, so that every letter appears in only one piece: all the copies of a letter end up in the same piece. The pieces, joined in order, give `s` back.

Implement `Pieces.sizes(String s)` in `Pieces.java` and return the lengths of the pieces, in order from the start of the string.

## Example 1

**Input:** `s = "hgfhgxyzzyklmlk"`
**Output:** `[5,1,4,5]`
**Explanation:** The pieces are `hgfhg`, `x`, `yzzy` and `klmlk`. A cut after `hgf` is not allowed even though `f` never appears again: the `h` and the `g` would be on both sides of it.

## Example 2

**Input:** `s = "nvwnwv"`
**Output:** `[6]`
**Explanation:** The `v` at position 1 appears again at the very end, so every cut would split it, and the whole string is one piece.

## Example 3

**Input:** `s = "wvu"`
**Output:** `[1,1,1]`
**Explanation:** No letter repeats, so every letter is a piece of its own.

## Constraints

- `1 <= s.length() <= 10^5`
- `s` contains only lowercase English letters.

## Follow-up

What if the letters were arbitrary ids, such as user names in a log?
