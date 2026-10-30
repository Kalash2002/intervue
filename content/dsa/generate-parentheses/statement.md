# Generate Parentheses

A string of `(` and `)` is balanced when every `(` is closed by a later `)` and no `)` comes before the `(` it closes: reading from the left, the number of `)` never gets ahead of the number of `(`, and the two are equal at the end.

Implement `Parentheses.generate(int n)` in `Parentheses.java`. Return every balanced string made of exactly `n` opening and `n` closing brackets, each once, in any order. Unlike some versions of this question, `n` may be 0.

## Example 1

**Input:** `n = 2`
**Output:** `["(())","()()"]` (in any order)
**Explanation:** Two pairs either nest, one inside the other, or stand side by side. `)(()` and `())(` have the right counts but close a bracket before it opens.

## Example 2

**Input:** `n = 4`
**Output:** `["(((())))","((()()))","((())())","((()))()","(()(()))","(()()())","(()())()","(())(())","(())()()","()((()))","()(()())","()(())()","()()(())","()()()()"]` (in any order)
**Explanation:** Fourteen of the seventy strings with four of each bracket are balanced.

## Example 3

**Input:** `n = 0`
**Output:** `[""]`
**Explanation:** With no brackets there is exactly one string, the empty one, and it is balanced, so the answer is a list holding the empty string, not an empty list.

## Constraints

- `0 <= n <= 8`

## Follow-up

How many strings are there for n pairs, and how would you count them without listing them?
