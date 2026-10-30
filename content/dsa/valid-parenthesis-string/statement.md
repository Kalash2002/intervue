# Valid Parenthesis String

A string `s` holds opening brackets `(`, closing brackets `)` and stars `*`. Each star may stand for an opening bracket, a closing bracket or nothing at all, chosen separately for every star. The brackets balance when every `(` is closed by a later `)` and every `)` closes an earlier `(`.

Implement `Wildcards.canBalance(String s)` in `Wildcards.java` and return `true` if some choice for the stars makes the brackets balance, and `false` otherwise.

## Example 1

**Input:** `s = "*)(()**"`
**Output:** `true`
**Explanation:** Read the first star as `(` for the `)` after it, the next star as `)` for the bracket still open, and the last star as nothing: `()(())`. No single reading works for every star: as `(` they leave three brackets open, and as `)` or as nothing the first `)` has nothing to close.

## Example 2

**Input:** `s = "*((*"`
**Output:** `false`
**Explanation:** The two `(` need two closers after them, and only the last star comes after them. The first star cannot close a bracket that opens later.

## Example 3

**Input:** `s = ")**"`
**Output:** `false`
**Explanation:** The `)` at the start has nothing before it to close, and both stars come after it.

## Constraints

- `1 <= s.length() <= 10^5`
- `s` contains only `(`, `)` and `*`.

## Follow-up

What if you also had to say what each star stands for?
