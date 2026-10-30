# Find Minimum in Rotated Sorted Array

A strictly increasing array was rotated: its first `r` values, for some `r` you are not told, were moved as a block to the end, keeping their order. The rotation may be zero, which leaves the array as it was. Given the rotated array `nums`, return its smallest value.

Implement `RotatedMinimum.min(int[] nums)` in `RotatedMinimum.java`. The tests call it many times on arrays of half a million values, so a scan of the whole array will be too slow.

## Example 1

**Input:** `nums = [31, 46, 52, 8, 14, 22]`
**Output:** `8`
**Explanation:** The sorted array `[8, 14, 22, 31, 46, 52]` had its first three values moved to the end.

## Example 2

**Input:** `nums = [14, 22, 31, 46, 52, 8]`
**Output:** `8`
**Explanation:** Only the first value, 8, was moved to the end, so the smallest value ends up last.

## Example 3

**Input:** `nums = [-5, 0, 6, 13]`
**Output:** `-5`
**Explanation:** The rotation was zero; the array is still in order and the first value is the smallest.

## Constraints

- `1 <= nums.length <= 10^6`
- `-2^31 <= nums[i] <= 2^31 - 1`
- All values of `nums` are distinct.
- `nums` is a strictly increasing array rotated by `r` positions, where `0 <= r < nums.length`.

## Follow-up

Can you find it in O(log n) time?
