# Contains Duplicate

You are given an array of integers `nums`. Decide whether some value occurs at two or more different positions.

Implement `ContainsDuplicate.hasDuplicate(int[] nums)` in `ContainsDuplicate.java`. Return `true` if some value repeats and `false` if every value is different.

## Example 1

**Input:** `nums = [7, 2, 9, 7]`
**Output:** `true`
**Explanation:** The value 7 sits at positions 0 and 3, so a value repeats. The other values appear once.

## Example 2

**Input:** `nums = [14, -3, 0, 8]`
**Output:** `false`
**Explanation:** The four values are all different. Negative numbers and zero count like any other value.

## Example 3

**Input:** `nums = []`
**Output:** `false`
**Explanation:** An empty array has no positions to compare, so nothing can repeat.

## Constraints

- `0 <= nums.length <= 3 * 10^5`
- `-2^31 <= nums[i] <= 2^31 - 1`

## Follow-up

Can you beat O(n²) time?
