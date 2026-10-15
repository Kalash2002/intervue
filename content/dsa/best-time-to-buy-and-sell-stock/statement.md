# Best Time to Buy and Sell Stock

`prices[i]` is the price of one share of a stock on day `i`. You may buy one share on one day and sell it on a strictly later day, or make no trade at all.

Implement `Trader.bestProfit(int[] prices)` in `Trader.java`. Return the largest profit you can make, or `0` if no trade makes money.

## Examples

- `prices = [9, 4, 12, 6, 15, 3]` returns `11`: buy at 4 on day 1, sell at 15 on day 4.
- `prices = [8, 6, 5, 2]` returns `0`: the price only falls, so the best choice is not to trade.
- `prices = [6, 5, 4, 1, 3]` returns `2`: buy at 1, sell at 3. The highest price (6) comes before the lowest, so it cannot be used.

## Constraints

- 0 <= `prices.length` <= 200,000
- 0 <= `prices[i]` <= 10,000
