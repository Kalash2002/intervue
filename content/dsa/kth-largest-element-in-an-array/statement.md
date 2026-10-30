# Kth Largest Element in an Array

You are given an array of integers `nums` and a number `k`. Return the value that would be at position `k` if the array were sorted from largest to smallest, counting from 1. Repeated values count separately: in `[5, 5, 2]` the largest is 5, the second largest is also 5, and the third largest is 2.

Implement `KthElement.kthLargest(int[] nums, int k)` in `KthElement.java`. You may reorder `nums` while you work. The tests call it on arrays of 100,000 values, some already sorted and some made of one value repeated, so a method whose speed depends on the order or on repeats can be too slow.

## Example 1

**Input:** `nums = [12, 5, 30, 7, 19]`, `k = 2`
**Output:** `19`
**Explanation:** From largest to smallest the values are 30, 19, 12, 7, 5, and the second one is 19.

## Example 2

**Input:** `nums = [8, 3, 8, 8, 1]`, `k = 3`
**Output:** `8`
**Explanation:** From largest to smallest the values are 8, 8, 8, 3, 1. The three 8s are separate values, so the third largest is still 8.

## Example 3

**Input:** `nums = [4, -2, 9]`, `k = 3`
**Output:** `-2`
**Explanation:** When `k` equals the length of the array, the k-th largest is the smallest value.

## Constraints

- `1 <= k <= nums.length <= 10^5`
- `-10^9 <= nums[i] <= 10^9`

## Follow-up

Can you guarantee linear time in the worst case, not only on average?
