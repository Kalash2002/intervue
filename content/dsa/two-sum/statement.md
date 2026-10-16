# Two Sum

You are given an array of integers `nums` and an integer `target`. Exactly one pair of different positions holds values that add up to `target`. Return those two positions.

Implement `TwoSum.indices(int[] nums, int target)` in `TwoSum.java`. Return an array of length 2 with the smaller position first. You may not use the same position twice, but the same value may appear at two positions.

## Example 1

**Input:** `nums = [4, 9, 2, 6]`, `target = 10`
**Output:** `[0, 3]`
**Explanation:** 4 + 6 = 10, and no other pair of positions adds up to 10.

## Example 2

**Input:** `nums = [-5, 8, 2, 13]`, `target = 8`
**Output:** `[0, 3]`
**Explanation:** -5 + 13 = 8. Negative values pair like any other.

## Example 3

**Input:** `nums = [3, 3]`, `target = 6`
**Output:** `[0, 1]`
**Explanation:** The two 3s sit at different positions, so they form the pair. One 3 used twice would not count.

## Constraints

- 2 <= `nums.length` <= 300,000
- Values and `target` fit in an `int`, and the sum of any two values does not overflow an `int`.
- Exactly one valid pair exists.

## Follow-up

Checking every pair takes O(n²) time. Can you find the pair in one pass over the array?
