# Pow(x, n)

Raise a floating-point number `x` to an integer power `n`. For a positive `n` that is `x` multiplied by itself `n` times; `x` to the power 0 is 1; and for a negative `n` it is 1 divided by `x` to the power `-n`. Zero to the power 0, or to a negative power, is undefined, so `x` is never 0 unless `n` is positive.

Implement `Power.raise(double x, int n)` in `Power.java` and return `x` to the power `n`. Compute it yourself: do not call `Math.pow`, `Math.exp`, `Math.log` or their `StrictMath` versions. `n` can be any `int`, the smallest one included, whose negation does not fit in an `int`.

The last digits of a `double` result depend on the order of the multiplications, so an answer is accepted when it is close to the exact value: `|answer - exact| <= 10^-6 * |exact| + 10^-12`, a relative error of at most 10^-6 with an absolute floor of 10^-12 for results near 0.

## Example 1

**Input:** `x = 1.5`, `n = 9`
**Output:** `38.443359375`
**Explanation:** 1.5 is 3/2, so the answer is 3^9 / 2^9 = 19683 / 512, which a `double` holds exactly.

## Example 2

**Input:** `x = -0.5`, `n = -11`
**Output:** `-2048.0`
**Explanation:** A negative power is a reciprocal: (-0.5)^11 = -1/2048, and 1 divided by that is -2048. An odd power of a negative number is negative.

## Example 3

**Input:** `x = -1.0`, `n = -2147483648`
**Output:** `1.0`
**Explanation:** This is the smallest `int`. Its negation, 2147483648, does not fit in an `int`, and it is even, so the answer is 1.

## Constraints

- `-100.0 < x < 100.0`
- `-2^31 <= n <= 2^31 - 1`
- `x != 0 || n > 0`
- `-10^4 <= x^n <= 10^4`

## Follow-up

Can you do it with O(log |n|) multiplications?
