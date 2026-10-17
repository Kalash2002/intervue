# Permutations

You are given an array of distinct whole numbers. An ordering of the array is a list that holds every one of its values exactly once, in some order; `[1, 6]` and `[6, 1]` are two different orderings.

Implement `Permutations.all(int[] nums)` in `Permutations.java`. Return every ordering of `nums` exactly once, each as a list. The orderings may be returned in any order, but the values inside each ordering must be in the order that ordering describes.

## Example 1

**Input:** `nums = [5, 0, 2]`
**Output:** `[[5, 0, 2], [5, 2, 0], [0, 5, 2], [0, 2, 5], [2, 5, 0], [2, 0, 5]]` (in any order)
**Explanation:** Any of the 3 values can go first, either of the other 2 second, and the last one fills the end: 3 × 2 × 1 = 6 orderings.

## Example 2

**Input:** `nums = [-3, 8]`
**Output:** `[[-3, 8], [8, -3]]` (in any order)
**Explanation:** Two values can stand in two orders.

## Example 3

**Input:** `nums = [42]`
**Output:** `[[42]]`
**Explanation:** A single value has one ordering, itself.

## Constraints

- `1 <= nums.length <= 8`
- `-50 <= nums[i] <= 50`
- All values in `nums` are distinct.

## Follow-up

Can you produce the next ordering from the current one without recursion?
