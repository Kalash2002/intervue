# Top K Frequent Elements

Given an array of integers `nums` and a number `k`, return the `k` values that appear most often in `nums`. The input is chosen so that the answer is unique: the `k`-th most frequent value is strictly more frequent than the next one.

Implement `TopKFrequent.topK(int[] nums, int k)` in `TopKFrequent.java`. Return an array of length `k`; the order of its values does not matter.

## Examples

- `nums = [5, 5, 5, 2, 2, 9]`, `k = 2` returns `[5, 2]` in any order: 5 appears three times and 2 twice.
- `nums = [8]`, `k = 1` returns `[8]`.
- `nums = [-1, -1, 4, 4, 4, 0, 0, 0, 0]`, `k = 1` returns `[0]`: it appears four times.

## Constraints

- 1 <= `nums.length` <= 200,000
- 1 <= `k` <= the number of distinct values in `nums`
- The answer is unique, as described above.
