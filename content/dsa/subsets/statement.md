# Subsets

You are given an array of distinct whole numbers. A subset is any selection of those values, from none of them to all of them; the order inside a subset does not matter, so `[2, 9]` and `[9, 2]` are the same subset.

Implement `Subsets.all(int[] nums)` in `Subsets.java`. Return every subset of `nums` exactly once, each as a list of its values. The empty subset counts. The values inside a subset may be in any order, and the subsets may be returned in any order.

## Example 1

**Input:** `nums = [4, -2, 7]`
**Output:** `[[], [4], [-2], [7], [4, -2], [4, 7], [-2, 7], [4, -2, 7]]` (in any order)
**Explanation:** Each of the three values is either in or out, which gives 2 × 2 × 2 = 8 subsets.

## Example 2

**Input:** `nums = [9]`
**Output:** `[[], [9]]`
**Explanation:** One value gives two subsets: without it and with it.

## Example 3

**Input:** `nums = []`
**Output:** `[[]]`
**Explanation:** An empty array still has one subset, the empty one, so the answer is a list holding one empty list, not an empty list.

## Constraints

- `0 <= nums.length <= 12`
- `-100 <= nums[i] <= 100`
- All values in `nums` are distinct.

## Follow-up

How would you produce the subsets one at a time, without holding them all in memory?
