# Maximum Subarray

A market stall keeps a ledger with one entry per trading day: `net[i]` is what day `i` made, negative on a day that lost money. Any run of one or more consecutive days can be reported on its own, and its total is the sum of its entries.

Implement `Ledger.bestRun(int[] net)` in `Ledger.java` and return the largest total of any run. A run has at least one day, so when every day lost money the answer is negative.

## Example 1

**Input:** `net = [3,-7,5,-2,6,-10,4]`
**Output:** `9`
**Explanation:** Days 2 to 4 give 5 - 2 + 6 = 9. Reaching back to day 0 adds 3 - 7 = -4, and reaching on to day 6 adds -10 + 4 = -6, so both make the run worse.

## Example 2

**Input:** `net = [-3,8,-9,2,5]`
**Output:** `8`
**Explanation:** Day 1 alone gives 8. Days 3 and 4 give 7, and joining the two runs across the -9 gives only 6.

## Example 3

**Input:** `net = [-6,-2,-8]`
**Output:** `-2`
**Explanation:** Every day lost money, so the best run is the single day that lost the least.

## Constraints

- `1 <= net.length <= 10^5`
- `-10^4 <= net[i] <= 10^4`
- `-10^9 <= total of any run <= 10^9`

## Follow-up

What if you also had to say which days the best run covers?
