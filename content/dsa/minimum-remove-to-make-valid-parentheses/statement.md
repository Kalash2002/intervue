# Minimum Remove to Make Valid Parentheses

A text is made of lowercase letters, `(` and `)`. Its round brackets are balanced when every `)` closes an earlier `(` and every `(` is closed. Letters never matter for balance.

Remove as few brackets as needed so that the text becomes balanced, and return it. Letters are never removed and nothing is reordered.

Several different choices of brackets can reach the same minimum, so the choice is fixed by this rule. Scan left to right and pair each `)` with the nearest earlier `(` that is not yet paired, if there is one. Every bracket left unpaired is removed and every paired bracket stays. This always removes the fewest brackets.

Implement `Expression.tidy(String text)` in `Expression.java`.

## Examples

- `text = "go(a(b)c"` returns `"goa(b)c"`: the `)` pairs with the second `(`, so the first `(` is unpaired and is removed.
- `text = "x)y)(z"` returns `"xyz"`: both `)` have nothing to close and the `(` is never closed.
- `text = "((a))("` returns `"((a))"`: only the last `(` is unpaired.

## Constraints

- 0 <= `text.length()` <= 400,000
- `text` contains only lowercase letters, `(` and `)`.
