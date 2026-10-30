# Sum of Two Integers

A small controller's instruction set has AND, OR, XOR, NOT and shifts, but no instruction that adds or subtracts. Write its addition: given two `int`s `a` and `b`, return their sum, using only the bitwise operators `&`, `|`, `^`, `~`, `<<`, `>>` and `>>>`, comparisons, and ordinary control flow.

The operators `+` and `-` are not allowed in any form: not as `++`, `--`, `+=` or `-=`, which rules out `i++` in a loop header, and not as a minus sign that negates a value, as in `-b`; a negative constant such as `-1` is fine. Nor are library methods that add or subtract for you, such as `Integer.sum`, `Math.addExact`, `Math.subtractExact`, `Math.incrementExact` or `BigInteger.add`.

The result is the one Java's `a + b` gives, also when the true sum does not fit in an `int`: it then wraps around by 2^32, so `Integer.MAX_VALUE` plus 1 would be `Integer.MIN_VALUE`, as in Example 3, and the same holds the other way for sums below `Integer.MIN_VALUE`.

Implement `LogicAdder.add(int a, int b)` in `LogicAdder.java`.

## Example 1

**Input:** `a = 19`, `b = 45`
**Output:** `64`
**Explanation:** 19 is 010011 and 45 is 101101. Bit 0 is 1 in both, which makes a carry, and the carry runs up through bits 1 to 5, where exactly one of the two has a 1, until it stops in bit 6: 1000000 is 64.

## Example 2

**Input:** `a = -38`, `b = 25`
**Output:** `-13`
**Explanation:** A negative `int` is stored in two's complement, and adding its bits to those of a positive number gives the bits of the sum with no special case for the signs.

## Example 3

**Input:** `a = 2147483000`, `b = 1000`
**Output:** `-2147483296`
**Explanation:** The true sum, 2,147,484,000, is `Integer.MAX_VALUE` plus 353. It wraps around as Java's `+` does: one past `Integer.MAX_VALUE` is `Integer.MIN_VALUE`, so the result is `Integer.MIN_VALUE` plus 352.

## Constraints

- `-2^31 <= a <= 2^31 - 1`
- `-2^31 <= b <= 2^31 - 1`

## Follow-up

Can you do it in the same small number of steps for every pair, even when a carry has to run through all 32 bits?
