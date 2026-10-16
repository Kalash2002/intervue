# Evaluate Reverse Polish Notation

In postfix notation each operator comes after the two values it applies to, so no brackets are needed: `9 3 -` means `9 - 3`, and `9 3 - 4 *` means `(9 - 3) * 4`. The expression is given as an array of tokens. Each token is either an integer (possibly negative, such as `"-5"`) or one of the operators `"+"`, `"-"`, `"*"` and `"/"`.

Implement `Postfix.evaluate(String[] tokens)` in `Postfix.java`. Return the value of the expression as an `int`. Division discards the fractional part, rounding towards zero, so `7 / -2` is `-3` and `-7 / 2` is `-3`. For `-` and `/` the value that came first is the left operand: `3 9 -` is `3 - 9`.

## Example 1

**Input:** `tokens = ["9", "3", "-", "4", "*"]`
**Output:** `24`
**Explanation:** Nine minus three is six, and six times four is 24.

## Example 2

**Input:** `tokens = ["7", "-2", "/"]`
**Output:** `-3`
**Explanation:** Seven divided by minus two is -3.5, which rounds towards zero to -3.

## Example 3

**Input:** `tokens = ["-5", "-3", "-"]`
**Output:** `-2`
**Explanation:** The tokens `"-5"` and `"-3"` are numbers; only the last `"-"` is an operator. The value is -5 - (-3) = -2.

## Example 4

**Input:** `tokens = ["42"]`
**Output:** `42`
**Explanation:** A single value is its own result.

## Constraints

- `1 <= tokens.length <= 10^5`
- Each token is `"+"`, `"-"`, `"*"` or `"/"`, or an integer `v` written in decimal with `-10^5 <= v <= 10^5`.
- The expression is valid: every operator has two values to work on, and exactly one value is left at the end.
- No division by zero occurs, and every intermediate result fits in a 32-bit `int`.

## Follow-up

Can you beat O(n²) time, where n is `tokens.length`?
