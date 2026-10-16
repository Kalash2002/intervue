# Search in Rotated Sorted Array

A strictly increasing array was rotated: its first `r` values, for some `r` you are not told, were moved as a block to the end, keeping their order. The rotation may be zero. Given the rotated array `nums` and a `target`, return the index of `target` in `nums`, or `-1` when it does not occur.

Implement `RotatedSearch.indexOf(int[] nums, int target)` in `RotatedSearch.java`. The tests call it many times on arrays of half a million values, so a scan from the left will be too slow.

## Example 1

**Input:** `nums = [27, 33, 40, 51, 2, 9, 15]`, `target = 9`
**Output:** `5`
**Explanation:** The sorted array `[2, 9, 15, 27, 33, 40, 51]` had its first three values moved to the end, and 9 is now at index 5.

## Example 2

**Input:** `nums = [27, 33, 40, 51, 2, 9, 15]`, `target = 30`
**Output:** `-1`
**Explanation:** 30 would sit between 27 and 33, but it is not in the array.

## Example 3

**Input:** `nums = [9, 3]`, `target = 3`
**Output:** `1`
**Explanation:** The sorted array `[3, 9]` was rotated by one. With two values, the middle of the range is its first index, so a search must still decide correctly which half is in order.

## Constraints

- `1 <= nums.length <= 10^6`
- `-2^31 <= nums[i], target <= 2^31 - 1`
- All values of `nums` are distinct.
- `nums` is a strictly increasing array rotated by `r` positions, where `0 <= r < nums.length`.

## Follow-up

Can you find it in O(log n) time with a single binary search?
