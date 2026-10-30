# Reverse Integer

A display panel was wired with its digit positions in the opposite order, so every number shows up with its decimal digits reversed. Given an `int` `x`, return the number the panel shows: the digits of `x` in reverse order, with the sign of `x`. Zeros at the end of `x` become leading zeros and are dropped, so 5800 shows as 85.

The panel holds only `int` values. When the reversed number lies outside the `int` range, from `Integer.MIN_VALUE` to `Integer.MAX_VALUE`, return 0.

Implement `DigitMirror.reverse(int x)` in `DigitMirror.java`.

## Example 1

**Input:** `x = 58301`
**Output:** `10385`
**Explanation:** The digits 5, 8, 3, 0, 1 read backwards are 1, 0, 3, 8, 5.

## Example 2

**Input:** `x = -9400`
**Output:** `-49`
**Explanation:** The sign stays in front. The two zeros at the end of 9400 come first once reversed and are dropped.

## Example 3

**Input:** `x = 1563847412`
**Output:** `0`
**Explanation:** Reversed, the digits give 2,147,483,651, which is 4 more than `Integer.MAX_VALUE`, so it does not fit and the answer is 0.

## Constraints

- `-2^31 <= x <= 2^31 - 1`

## Follow-up

Can you do it with `int` arithmetic only, never holding a value outside the `int` range?
