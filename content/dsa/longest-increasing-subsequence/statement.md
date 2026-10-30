# Longest Increasing Subsequence

You are given an array of integers `nums`. Pick as many of its positions as you can, keeping them in their original order, so that every picked value is strictly larger than the value picked just before it. The picked positions do not have to be next to each other, and a value equal to the one before it does not count as larger.

Implement `Rising.longest(int[] nums)` in `Rising.java` and return the largest number of positions you can pick.

## Example 1

**Input:** `nums = [4,9,5,2,6,3,8]`
**Output:** `4`
**Explanation:** 4, 5, 6, 8 at positions 0, 2, 4 and 6. Picking the 9 right after the 4 leaves nothing larger than 9 to follow it.

## Example 2

**Input:** `nums = [-3,10,-1,0,0,2]`
**Output:** `4`
**Explanation:** -3, -1, 0, 2. The two 0s cannot both be picked, because the second is not larger than the first.

## Example 3

**Input:** `nums = [12,8,5,1]`
**Output:** `1`
**Explanation:** Every value is smaller than the one before it, so any single value is a longest pick.

## Constraints

- `1 <= nums.length <= 2 * 10^5`
- `-10^9 <= nums[i] <= 10^9`

## Follow-up

What if you also had to return one longest subsequence, not only its length?
