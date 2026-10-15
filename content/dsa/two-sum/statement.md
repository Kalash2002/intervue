# Two Sum

You are given an array of integers `nums` and an integer `target`. Exactly one pair of different positions holds values that add up to `target`. Return those two positions.

Implement `TwoSum.indices(int[] nums, int target)` in `TwoSum.java`. Return an array of length 2 with the smaller position first. You may not use the same position twice, but the same value may appear at two positions.

## Examples

- `nums = [4, 9, 2, 6]`, `target = 10` returns `[0, 3]`, because 4 + 6 = 10.
- `nums = [3, 3]`, `target = 6` returns `[0, 1]`: two equal values at different positions are fine.
- `nums = [-5, 8, 2, 13]`, `target = 8` returns `[0, 3]`, because -5 + 13 = 8.

## Constraints

- 2 <= `nums.length` <= 300,000
- Values and `target` fit comfortably in an `int`; sums of two values do not overflow.
- Exactly one valid pair exists.
