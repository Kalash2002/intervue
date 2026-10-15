# How a strong round goes: Best Time to Buy and Sell Stock

## Approach

Take any selling day. The best profit for that day comes from buying on the cheapest earlier day, so for each day I only need the lowest price seen before it. That lets me walk the list once: keep the lowest price so far, compare the current price against it, and keep the best difference. I update the lowest price after the comparison, because a day cannot be both the buy and the sell. The tempting shortcut of subtracting the smallest price in the whole list from the largest fails whenever the high comes first, as in `[6, 5, 4, 1, 3]`, where it gives 5 and the real answer is 2. An empty list or a falling list leaves the best at 0, which is the "do not trade" answer.

```java
static int bestProfit(int[] prices) {
    int best = 0;
    int lowest = Integer.MAX_VALUE; // cheapest price seen on an earlier day
    for (int price : prices) {
        best = Math.max(best, price - lowest); // sell on this day against the cheapest earlier day
        lowest = Math.min(lowest, price);
    }
    return best;
}
```

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Try every buy day and every later sell day | O(n squared) | O(1) |
| One pass with the lowest price so far | O(n) | O(1) |

## Where it shows up

Finding the largest rise between an earlier and a later point appears in market and metrics dashboards (the biggest gain over a period) and in monitoring, where "largest increase since the quietest moment" is a standard way to flag a ramp. The pattern of carrying a running best from the left is common wherever a later value is compared with the best earlier one.

## Follow-up answers

**What changes if you may complete as many buy-and-sell trades as you like, one share at a time?** Then every rise between neighbouring days can be collected: add `prices[i] - prices[i-1]` whenever it is positive. You never hold more than one share, and splitting a long climb into daily steps earns the same total.

**How would you also return the buy day and the sell day?** Remember the day of the lowest price, and whenever a new best profit is found, record that remembered day as the buy day and the current day as the sell day. Update the remembered day only when the lowest price changes.

**What if each trade costs a fixed fee?** Track two values as you go: the best profit while holding nothing, and the best profit while holding a share. Buying subtracts the price, selling adds the price and subtracts the fee, and each day updates both from the previous day's values.
