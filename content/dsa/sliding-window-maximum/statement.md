# Sliding Window Maximum

A window of width `k` slides over an array of integers from the left end to the right end, one position at a time. At each position the window covers `k` consecutive elements. Report the largest element inside the window at every position.

Implement `WindowMax.maxima(int[] nums, int k)` in `WindowMax.java`. Return an array of length `nums.length - k + 1` where entry `i` is the maximum of `nums[i]` to `nums[i + k - 1]`.

## Example 1

**Input:** `nums = [2, 9, 4, 1, 6, 3]`, `k = 3`
**Output:** `[9, 9, 6, 6]`
**Explanation:** The windows are `[2, 9, 4]`, `[9, 4, 1]`, `[4, 1, 6]` and `[1, 6, 3]`, with maxima 9, 9, 6 and 6.

## Example 2

**Input:** `nums = [5, 5, 5]`, `k = 2`
**Output:** `[5, 5]`
**Explanation:** Equal values are fine: each of the two windows has maximum 5.

## Example 3

**Input:** `nums = [-3, -1, -2]`, `k = 1`
**Output:** `[-3, -1, -2]`
**Explanation:** With a window of one, every element is its own maximum, negative or not.

## Constraints

- `1 <= k <= nums.length <= 10^6`
- `-2^31 <= nums[i] <= 2^31 - 1`

## Follow-up

Can you beat O(`nums.length` * `k`) time?
