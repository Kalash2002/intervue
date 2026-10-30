# Minimum Remove to Make Valid Parentheses

A text is made of lowercase letters, `(` and `)`. Its round brackets are balanced when every `)` closes an earlier `(` and every `(` is closed. Letters never matter for balance.

Remove as few brackets as needed so that the text becomes balanced, and return it. Letters are never removed and nothing is reordered.

Several different choices of brackets can reach the same minimum, so the choice is fixed by this rule. Scan left to right and pair each `)` with the nearest earlier `(` that is not yet paired, if there is one. Every bracket left unpaired is removed and every paired bracket stays. This always removes the fewest brackets.

Implement `Expression.tidy(String text)` in `Expression.java`.

## Example 1

**Input:** `text = "go(a(b)c"`
**Output:** `"goa(b)c"`
**Explanation:** The `)` pairs with the second `(`, the nearest one before it, so the first `(` is unpaired and is removed.

## Example 2

**Input:** `text = "x)y)(z"`
**Output:** `"xyz"`
**Explanation:** Both `)` have nothing to close, and the `(` is never closed. All three go.

## Example 3

**Input:** `text = "((a))("`
**Output:** `"((a))"`
**Explanation:** The two pairs nest around the `a`, and only the last `(` is unpaired.

## Constraints

- `0 <= text.length() <= 4 * 10^5`
- `text[i]` is a lowercase letter, `(` or `)`

## Follow-up

How would you return the number of removed brackets instead of the text?
