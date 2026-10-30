# Single Number

You are given an array of integers in which every value appears exactly twice, except one value that appears exactly once. The two copies of a value can be anywhere in the array, not necessarily next to each other. Return the value that appears once.

Implement `LoneValue.find(int[] nums)` in `LoneValue.java`.

## Example 1

**Input:** `nums = [12, 5, 10, 12, 10]`
**Output:** `5`
**Explanation:** 12 and 10 each appear twice; 5 appears once.

## Example 2

**Input:** `nums = [0, 40, -7, 3, 40, 0, 3]`
**Output:** `-7`
**Explanation:** 0, 40 and 3 each appear twice, and no copy sits next to its partner. -7 appears once; a negative value is an ordinary value.

## Example 3

**Input:** `nums = [-30000]`
**Output:** `-30000`
**Explanation:** With one value there are no pairs, so that value is the answer, here the smallest one allowed.

## Constraints

- `1 <= nums.length <= 3 * 10^4`
- `-3 * 10^4 <= nums[i] <= 3 * 10^4`
- Every value in `nums` appears exactly twice, except one value that appears exactly once.

## Follow-up

Can you do it in O(n) time with only a constant amount of extra memory?
