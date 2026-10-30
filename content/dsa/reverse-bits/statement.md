# Reverse Bits

A sensor sends 32-bit words over a serial line lowest bit first, but the receiver files the bits it reads from the top position down, so every word arrives mirrored: bit 0 of the word sent ends up in bit 31, bit 1 in bit 30, and so on. Given a word as it was received, return it with its 32 bits in the opposite order, so that bit i of the input becomes bit 31 - i of the result.

Java has no unsigned `int`, so each word is passed and returned as an ordinary `int` in two's complement: a word whose top bit, bit 31, is 1 is a negative number, in the input and in the result alike, as in Examples 1 to 3. All 32 positions take part, including the 0 bits above the highest 1: the word 6, which is 110 with 29 zeros above it, mirrors to 011 followed by 29 zeros, which is 1610612736.

Implement `WireOrder.mirror(int word)` in `WireOrder.java`. Do not call `Integer.reverse` or `Long.reverse`, which mirror the bits for you.

## Example 1

**Input:** `word = 37`
**Output:** `-1543503872`
**Explanation:** 37 is 00000000 00000000 00000000 00100101. Mirrored it is 10100100 00000000 00000000 00000000, whose top bit is 1, so Java reads it as -1543503872.

## Example 2

**Input:** `word = -16`
**Output:** `268435455`
**Explanation:** -16 is 11111111 11111111 11111111 11110000. Mirrored it is 00001111 11111111 11111111 11111111, which is 2^28 - 1.

## Example 3

**Input:** `word = 1`
**Output:** `-2147483648`
**Explanation:** The only 1 bit, bit 0, moves to bit 31, the top bit, so the result is the smallest `int`.

## Constraints

- `-2^31 <= word <= 2^31 - 1`

## Follow-up

Can you do it in fewer than 32 steps, without a loop over the bits?
