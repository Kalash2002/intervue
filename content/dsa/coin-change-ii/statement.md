# Coin Change II

A vending machine pays out change from coins of the values in `coins`, with as many coins of each value as it needs. Two ways of paying are the same when they use the same number of coins of each value: the order in which the coins drop out does not matter.

Implement `Purse.ways(int[] coins, int amount)` in `Purse.java` and return the number of different ways to pay exactly `amount`. Paying `0` uses no coins, and that is one way.

The count for `amount` always fits an `int` (see Constraints), but the counts for smaller totals need not: with some coin values they run far past it, even past what a `long` holds. Only the count for `amount` is checked.

## Example 1

**Input:** `coins = [3,5,8]`, `amount = 16`
**Output:** `3`
**Explanation:** 8 + 8, 8 + 5 + 3 and 5 + 5 + 3 + 3. Counting every order in which the coins could drop out would give 13, but the order does not matter.

## Example 2

**Input:** `coins = [4,9,7]`, `amount = 18`
**Output:** `2`
**Explanation:** 9 + 9 and 7 + 7 + 4. A value can be used more than once, and the values need not come sorted.

## Example 3

**Input:** `coins = [4,9]`, `amount = 0`
**Output:** `1`
**Explanation:** Paying nothing takes no coins at all, and that counts as one way.

## Constraints

- `1 <= coins.length <= 300`
- `1 <= coins[i] <= 5000`
- All values in `coins` are different.
- `0 <= amount <= 5000`
- The answer is less than `2^31`.

## Follow-up

What if the order of the coins mattered, so that 5 + 3 and 3 + 5 counted as two ways?
