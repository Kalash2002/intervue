# Product of Array Except Self

You are given an array of whole numbers `nums`. For each position, you want the product of every number in the array except the one at that position.

Implement `Products.exceptSelf(int[] nums)` in `Products.java`. Return a new array `out` of the same length where `out[i]` is the product of all `nums[j]` with `j != i`. Do not change `nums`.

## Example 1

**Input:** `nums = [2, 5, 3]`
**Output:** `[15, 6, 10]`
**Explanation:** Position 0 leaves out the 2 and multiplies `5 * 3 = 15`. Position 1 leaves out the 5 and gives `2 * 3 = 6`. Position 2 leaves out the 3 and gives `2 * 5 = 10`.

## Example 2

**Input:** `nums = [4, 0, 6]`
**Output:** `[0, 24, 0]`
**Explanation:** Every product that includes the zero is zero, so positions 0 and 2 give 0. Position 1 leaves the zero out, so its product is `4 * 6 = 24`.

## Example 3

**Input:** `nums = [-2, 7]`
**Output:** `[7, -2]`
**Explanation:** With only two numbers, each slot is the other single number, signs included.

## Constraints

- `2 <= nums.length <= 2 * 10^5`
- `-10 <= nums[i] <= 10`
- Every value of `out` fits in a 32-bit signed integer.

## Follow-up

Can you do it in O(n) time and O(1) extra space, not counting the output?
