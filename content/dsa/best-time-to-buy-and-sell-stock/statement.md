# Best Time to Buy and Sell Stock

`prices[i]` is the price of one share of a stock on day `i`. You may buy one share on one day and sell it on a strictly later day, or make no trade at all.

Implement `Trader.bestProfit(int[] prices)` in `Trader.java`. Return the largest profit you can make, or `0` if no trade makes money.

## Example 1

**Input:** `prices = [9, 4, 12, 6, 15, 3]`
**Output:** `11`
**Explanation:** Buy at 4 on day 1 and sell at 15 on day 4, for a profit of 11.

## Example 2

**Input:** `prices = [6, 5, 4, 1, 3]`
**Output:** `2`
**Explanation:** Buy at 1 and sell at 3. The highest price (6) comes before the lowest, so the two cannot be used together.

## Example 3

**Input:** `prices = [8, 6, 5, 2]`
**Output:** `0`
**Explanation:** The price only falls, so every trade loses money and the best choice is not to trade.

## Constraints

- `0 <= prices.length <= 200000`
- `0 <= prices[i] <= 10000`

## Follow-up

Can you do it in one pass over the prices with O(1) extra memory?
