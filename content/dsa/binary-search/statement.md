# Binary Search

You are given an array `nums` whose values are strictly increasing, and a `target`. Return the index of `target` in `nums`, or `-1` when it does not occur.

Implement `SortedLookup.indexOf(int[] nums, int target)` in `SortedLookup.java`. The tests call it many times on arrays of a million values, so a scan from the left will be too slow.

## Example 1

**Input:** `nums = [-7, -2, 3, 11, 25, 40]`, `target = 11`
**Output:** `3`
**Explanation:** 11 sits at index 3.

## Example 2

**Input:** `nums = [-7, -2, 3, 11, 25, 40]`, `target = 4`
**Output:** `-1`
**Explanation:** 4 would fall between 3 and 11, but it is not in the array.

## Example 3

**Input:** `nums = []`, `target = 5`
**Output:** `-1`
**Explanation:** An empty array holds nothing, so there is nothing to find.

## Constraints

- `0 <= nums.length <= 10^6`
- `nums[i] < nums[i + 1]` for every valid `i`
- `-2^31 <= nums[i], target <= 2^31 - 1`

## Follow-up

Can you answer each call in O(log n) time?
