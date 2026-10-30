# Valid Parentheses

A string is made only of the six characters `(`, `)`, `[`, `]`, `{` and `}`. It is balanced when every opening bracket is closed by a bracket of the same kind, and brackets close in the reverse order they were opened.

Implement `Brackets.isBalanced(String s)` in `Brackets.java`. Return `true` when the string is balanced and `false` otherwise. The empty string is balanced.

## Example 1

**Input:** `s = "[()]{}()"`
**Output:** `true`
**Explanation:** The round pair closes inside the square one, then two more pairs follow one after another, so every bracket meets its partner in the right order.

## Example 2

**Input:** `s = "[{]}"`
**Output:** `false`
**Explanation:** The square bracket is closed while the curly one inside it is still open.

## Example 3

**Input:** `s = ""`
**Output:** `true`
**Explanation:** There is nothing to close, so the empty string is balanced.

## Constraints

- `0 <= s.length() <= 4 * 10^5`
- Each character of `s` is one of `(`, `)`, `[`, `]`, `{` or `}`.

## Follow-up

Can you beat O(n²) time?
