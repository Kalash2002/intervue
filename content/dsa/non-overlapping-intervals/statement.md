# Non-overlapping Intervals

An observatory's telescope serves one observation request at a time. Each request is a pair `[start, end)` of minutes: it needs the telescope from `start` up to but not including `end`. Two requests overlap when one starts before the other ends. A request that ends at minute 30 and another that starts at minute 30 do not overlap: both can run, one after the other.

Implement `Telescope.minCancellations(int[][] requests)` in `Telescope.java`. Return the smallest number of requests to cancel so that no two of the remaining requests overlap. The requests come in no particular order.

## Example 1

**Input:** `requests = [[9,15],[1,4],[3,8],[12,13],[4,7],[14,20]]`
**Output:** `2`
**Explanation:** `[1,4]` and `[3,8]` overlap, so one of them must go, and so must one of `[9,15]` and `[12,13]`. Cancelling `[3,8]` and `[9,15]` is enough: `[1,4]`, `[4,7]`, `[12,13]` and `[14,20]` remain, and `[1,4]` and `[4,7]` only touch.

## Example 2

**Input:** `requests = [[5,16],[15,19],[18,22]]`
**Output:** `1`
**Explanation:** `[15,19]` overlaps both of the others, which do not overlap each other, so cancelling it is enough. Cancelling the longest request, `[5,16]`, would leave the other two still overlapping.

## Example 3

**Input:** `requests = [[7,19],[7,19]]`
**Output:** `1`
**Explanation:** Two identical requests overlap each other completely, so one of them must go.

## Constraints

- `1 <= requests.length <= 10^5`
- `0 <= start < end <= 10^9` for every request

## Follow-up

What if each request carries a value, and you must keep the most total value instead of the most requests?
