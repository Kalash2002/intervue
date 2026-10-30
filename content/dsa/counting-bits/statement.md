# Counting Bits

A decoder looks up how many 1 bits a small value has in a table, instead of counting them each time. Build that table: given `n`, return an array of length `n + 1` whose entry `i` is the number of 1 bits in the binary form of `i`, for every `i` from 0 to `n`.

Implement `BitTable.build(int n)` in `BitTable.java`.

## Example 1

**Input:** `n = 10`
**Output:** `[0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2]`
**Explanation:** 7 is 111 in binary, with three 1 bits; 8 is 1000, with one; 10 is 1010, with two. The table has 11 entries, for 0 to 10 with both ends included.

## Example 2

**Input:** `n = 16`
**Output:** `[0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4, 1]`
**Explanation:** 15 is 1111, with four 1 bits, and 16 is 10000, back to one: a power of two has a single 1 bit.

## Example 3

**Input:** `n = 0`
**Output:** `[0]`
**Explanation:** The table has a single entry, for 0, which has no 1 bits.

## Constraints

- `0 <= n <= 10^5`

## Follow-up

Can you fill the table in O(n) time, using entries already filled instead of counting each number's bits from scratch?
