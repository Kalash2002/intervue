# 3Sum

You are given an array of whole numbers. A zero triple is a group of three values, taken from three different positions, that add up to zero.

Implement `Triples.zeroSum(int[] nums)` in `Triples.java`. Return every distinct zero triple as a list of three numbers. Two triples are the same when they hold the same three values, however many positions can supply them, so each distinct triple appears once. The three numbers inside a triple may be in any order, and the triples may be returned in any order. If there are none, return an empty list. Do not modify `nums`.

## Example 1

**Input:** `nums = [-4, 1, 3, 0, 2, -1, 4]`
**Output:** `[[-4, 0, 4], [-4, 1, 3], [-1, 0, 1]]` (in any order)
**Explanation:** Each of the three triples adds up to zero, and no other group of three values does.

## Example 2

**Input:** `nums = [-2, -2, 0, 0, 2, 2]`
**Output:** `[[-2, 0, 2]]`
**Explanation:** Eight different groups of positions give the values -2, 0 and 2, but the triple is listed once.

## Example 3

**Input:** `nums = [0, 1, -1, 5]`
**Output:** `[[-1, 0, 1]]`
**Explanation:** There is a single 0, and it cannot be used three times, so `[0, 0, 0]` is not an answer.

## Constraints

- `0 <= nums.length <= 5000`
- `-10^5 <= nums[i] <= 10^5`

## Follow-up

Can you beat O(n³) time?
