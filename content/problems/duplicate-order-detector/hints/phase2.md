## Level 1

For each order you only need one fact about its basket's history: the minute of the most recent earlier copy. Think about what to remember as you walk the log once, in order.

## Level 2

Keep a map from basket key to the last minute it was seen. For each order, look up the previous minute, compare the gap with the window using a strict less-than, then store the current minute. For the SKU report, count repeats per SKU in a map while you walk, then sort the entries by count descending and SKU ascending.

## Level 3

1. Reject a window that is zero or negative before doing anything else.
2. Walk the orders oldest first; put each order's minute into the map and read back what was there before.
3. If there was a previous minute and the difference is smaller than the window, add the order id.
4. For the SKU report, reuse the "seen before" set from `exactRepeats` and increase a per-SKU counter on every repeat.
5. Sort the counts, keep the first `k`, and check the sample file: kettle and mug tie on two, the lamp has one.
