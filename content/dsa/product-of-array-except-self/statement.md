# Product of Array Except Self

You are given an array of whole numbers. For each position, you want the product of every number in the array except the one at that position.

Implement `Products.exceptSelf(int[] nums)` in `Products.java`. Return a new array `out` of the same length where `out[i]` is the product of all `nums[j]` with `j != i`. Do not change `nums`.

Dividing the total product by `nums[i]` is awkward because the array may contain zeros. Aim for a linear-time solution that does not divide.

## Examples

- `nums = [2, 5, 3]` returns `[15, 6, 10]`: `5 * 3`, `2 * 3` and `2 * 5`.
- `nums = [4, 0, 6]` returns `[0, 24, 0]`: every product that includes the zero is zero, and the zero's own slot is the product of the other two.
- `nums = [-2, 7]` returns `[7, -2]`: each slot is the other single number.

## Constraints

- 2 <= `nums.length` <= 200,000
- -10 <= `nums[i]` <= 10
- Every answer value fits in a 32-bit `int`.
