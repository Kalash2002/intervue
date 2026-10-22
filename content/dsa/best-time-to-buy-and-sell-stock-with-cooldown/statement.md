# Best Time to Buy and Sell Stock with Cooldown

`prices[i]` is the price of one share on day `i`. A trading desk may trade as often as it likes, one share at a time: it buys a share, sells it on a later day, and only then may buy again. Each day it does at most one thing, buy, sell or nothing. After every sale the desk sits out the next day: it may not buy on the day right after selling.

Implement `Desk.bestProfit(int[] prices)` in `Desk.java` and return the largest total profit the desk can make. Making no trade at all is allowed, so the answer is never negative.

## Example 1

**Input:** `prices = [6,2,9,4,8,3,12]`
**Output:** `16`
**Explanation:** Buy at 2 and sell at 9, sit out day 3, then buy at 3 and sell at 12, for 7 + 9. The rise from 4 to 8 cannot be added: buying at 4 falls on the rest day after selling at 9, and selling at 8 would make the day priced 3 a rest day.

## Example 2

**Input:** `prices = [2,7,6,11]`
**Output:** `9`
**Explanation:** Selling at 7 makes the next day, priced 6, a rest day, so the second rise cannot be bought. Keeping the share from 2 to 11 earns 9, more than either rise alone.

## Example 3

**Input:** `prices = [8,8,8,8]`
**Output:** `0`
**Explanation:** The price never moves, so no trade makes money and the best choice is not to trade.

## Constraints

- `1 <= prices.length <= 5000`
- `0 <= prices[i] <= 1000`

## Follow-up

What if every sale cost a fixed fee and there were no rest day?
