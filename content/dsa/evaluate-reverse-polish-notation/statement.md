# Evaluate Reverse Polish Notation

In postfix notation each operator comes after the two values it applies to, so no brackets are needed: `9 3 -` means `9 - 3`, and `9 3 - 4 *` means `(9 - 3) * 4`. The expression is given as an array of tokens. Each token is either an integer (possibly negative, such as `"-5"`) or one of the operators `"+"`, `"-"`, `"*"` and `"/"`.

Implement `Postfix.evaluate(String[] tokens)` in `Postfix.java`. Return the value of the expression as an `int`. Division discards the fractional part, rounding towards zero, so `7 / -2` is `-3` and `-7 / 2` is `-3`. For `-` and `/` the value that came first is the left operand: `3 9 -` is `3 - 9`.

## Examples

- `tokens = ["9", "3", "-", "4", "*"]` returns `24`: nine minus three is six, and six times four is 24.
- `tokens = ["7", "-2", "/"]` returns `-3`: seven divided by minus two is -3.5, which becomes -3.
- `tokens = ["42"]` returns `42`: a single value is its own result.

## Constraints

- 1 <= `tokens.length` <= 100,000
- The expression is valid: every operator has two values to work on, and exactly one value is left at the end
- No division by zero occurs, and every intermediate result fits in a 32-bit `int`
