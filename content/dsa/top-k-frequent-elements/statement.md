# Top K Frequent Elements

You are given an array of integers `nums` and a number `k`. Return the `k` values that occur most often in `nums`. The input is chosen so that the answer is unique: the `k`-th most frequent value occurs strictly more often than any value left out.

Implement `TopKFrequent.topK(int[] nums, int k)` in `TopKFrequent.java`. Return an array of length `k`; the order of its values does not matter.

## Example 1

**Input:** `nums = [6, -2, 6, 9, -2, 6, 4, 6, -2]`, `k = 2`
**Output:** `[6, -2]` (in any order)
**Explanation:** 6 occurs four times and -2 three times, while 9 and 4 occur once each. The two most frequent values are 6 and -2.

## Example 2

**Input:** `nums = [-1, -1, 4, 4, 4, 0, 0, 0, 0]`, `k = 1`
**Output:** `[0]`
**Explanation:** 0 occurs four times, 4 three times and -1 twice. Only the single most frequent value is wanted, and it is 0.

## Example 3

**Input:** `nums = [20, 20, 7]`, `k = 2`
**Output:** `[20, 7]` (in any order)
**Explanation:** There are only two distinct values, so `k` equals the number of distinct values and both are returned.

## Constraints

- `1 <= nums.length <= 2 * 10^5`
- `-2^31 <= nums[i] <= 2^31 - 1`
- `1 <= k <=` the number of distinct values in `nums`
- The answer is unique, as described above.

## Follow-up

Can you do better than O(n log n) time?
