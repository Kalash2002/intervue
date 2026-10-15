# Sliding Window Maximum

A window of width `k` slides over an array of integers from the left end to the right end, one position at a time. At each position, the window covers `k` consecutive elements. Report the largest element inside the window at every position.

Implement `WindowMax.maxima(int[] nums, int k)` in `WindowMax.java`. Return an array of length `nums.length - k + 1` where entry `i` is the maximum of `nums[i]` to `nums[i + k - 1]`.

## Examples

- `nums = [2, 9, 4, 1, 6, 3]`, `k = 3` returns `[9, 9, 6, 6]`: the windows are `[2, 9, 4]`, `[9, 4, 1]`, `[4, 1, 6]` and `[1, 6, 3]`.
- `nums = [5, 5, 5]`, `k = 2` returns `[5, 5]`: equal values are fine.
- `nums = [-3, -1, -2]`, `k = 1` returns `[-3, -1, -2]`: with a window of one, every element is its own maximum.

## Constraints

- 1 <= `k` <= `nums.length` <= 1,000,000
- Values are any `int`, negative values included.
