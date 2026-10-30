# Number of 1 Bits

A controller keeps 32 on/off switches in one `int`: bit i holds switch i, from bit 0, the lowest, to bit 31, the top bit, and a switch is on when its bit is 1. Return how many switches are on, that is, how many of the 32 bits are 1.

Java has no unsigned `int`, so the 32 bits arrive as an ordinary `int` in two's complement, and every one of the 2^32 patterns is a valid input. A pattern whose top bit is 1 is passed as a negative number, as in Examples 2 and 3. Count the 1 bits of the pattern, not the digits of the number.

Implement `SwitchBoard.countOn(int switches)` in `SwitchBoard.java`. Do not call `Integer.bitCount` or `Long.bitCount`, which count the bits for you.

## Example 1

**Input:** `switches = 1000`
**Output:** `6`
**Explanation:** 1000 is 1111101000 in binary, with 22 zeros above it, so switches 3, 5, 6, 7, 8 and 9 are on.

## Example 2

**Input:** `switches = -256`
**Output:** `24`
**Explanation:** The pattern is 11111111 11111111 11111111 00000000: bits 8 to 31 are on, and because bit 31 is one of them, Java reads the pattern as -256.

## Example 3

**Input:** `switches = -2147483648`
**Output:** `1`
**Explanation:** Only bit 31 is on. As an `int` this pattern is the smallest number there is, -2^31.

## Constraints

- `-2^31 <= switches <= 2^31 - 1`

## Follow-up

Can you make the work depend on the number of 1 bits instead of on all 32 positions?
