# Coin Change

A till holds coins of the values in `coins`, as many of each value as you like. You must pay exactly `amount` with as few coins as possible.

Implement `Till.fewestCoins(int[] coins, int amount)` in `Till.java` and return the smallest number of coins whose values add up to exactly `amount`. Return `-1` when no combination of the coins adds up to it. Paying `0` takes no coins.

## Example 1

**Input:** `coins = [2,5,6]`, `amount = 10`
**Output:** `2`
**Explanation:** Two 5s make 10. Starting with the largest coin, 6, leaves 4, which takes two 2s, so three coins.

## Example 2

**Input:** `coins = [4,7]`, `amount = 15`
**Output:** `3`
**Explanation:** 4 + 4 + 7 = 15. Taking as many 7s as fit leaves 1, which no coin can pay.

## Example 3

**Input:** `coins = [12,21]`, `amount = 40`
**Output:** `-1`
**Explanation:** Every total made of 12s and 21s is a multiple of 3, and 40 is not.

## Example 4

**Input:** `coins = [2147483647,3]`, `amount = 9`
**Output:** `3`
**Explanation:** The first coin is far larger than the amount and is never used, so three 3s pay the 9.

## Constraints

- `1 <= coins.length <= 12`
- `1 <= coins[i] <= 2^31 - 1`
- `0 <= amount <= 5000`
- All values in `coins` are different.

## Follow-up

What if you had to count the different combinations of coins that make the amount instead?
