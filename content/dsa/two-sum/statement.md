# Two Sum

You are given an array of integers `nums` and an integer `target`. Find the two different positions whose values add up to `target` and return them.

Implement `TwoSum.indices(int[] nums, int target)` in `TwoSum.java`. Return an array of length 2 holding the two positions, in either order. You may not use the same position twice, but the same value may appear at two positions.

## Example 1

**Input:** `nums = [4, 9, 2, 6]`, `target = 10`
**Output:** `[0, 3]`
**Explanation:** 4 + 6 = 10, and no other pair of positions adds up to 10.

## Example 2

**Input:** `nums = [-5, 8, 2, 13]`, `target = 8`
**Output:** `[0, 3]`
**Explanation:** -5 + 13 = 8. Negative values pair like any other.

## Example 3

**Input:** `nums = [7, 2, 7]`, `target = 14`
**Output:** `[0, 2]`
**Explanation:** The two 7s sit at different positions, so they form the pair. The pairs sum to 9, 14 and 9, so this is the only answer; one 7 used twice would not count.

## Constraints

- `2 <= nums.length <= 3 * 10^5`
- `-10^9 <= nums[i] <= 10^9`
- `-2 * 10^9 <= target <= 2 * 10^9`
- Exactly one valid pair exists.

## Follow-up

Can you beat O(n²) time?
