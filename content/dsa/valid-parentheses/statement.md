# Valid Parentheses

A string is made only of the six characters `(`, `)`, `[`, `]`, `{` and `}`. It is balanced when every opening bracket is closed by a bracket of the same kind, and brackets close in the reverse order they were opened.

Implement `Brackets.isBalanced(String s)` in `Brackets.java`. Return `true` when the string is balanced and `false` otherwise. The empty string is balanced.

## Examples

- `s = "([]){}"` returns `true`: each pair closes before its parent does.
- `s = "([)]"` returns `false`: the round bracket is closed while the square one is still open.
- `s = "(("` returns `false`: nothing closes the brackets.

## Constraints

- 0 <= `s.length()` <= 400,000
- `s` contains only `(`, `)`, `[`, `]`, `{` and `}`.
