# 3Sum

You are given an array of whole numbers. A zero triple is a group of three values, taken from three different positions, that add up to zero.

Implement `Triples.zeroSum(int[] nums)` in `Triples.java`. Return every distinct zero triple as a list of three numbers in non-decreasing order, such as `[-2, 0, 2]`. Two triples are the same when they hold the same three values, however many positions can supply them, so each distinct triple appears once. The triples may be returned in any order. If there are none, return an empty list.

## Examples

- `nums = [-4, 1, 3, 0, 2, -1, 4]` returns `[[-4, 0, 4], [-4, 1, 3], [-1, 0, 1]]` in any order.
- `nums = [-2, -2, 0, 0, 2, 2]` returns `[[-2, 0, 2]]`: four positions could supply the pair `-2` and `2`, but the triple is listed once.
- `nums = [0, 1, -1, 5]` returns `[[-1, 0, 1]]`: a single `0` cannot be used three times.

## Constraints

- 0 <= `nums.length` <= 5,000
- -100,000 <= `nums[i]` <= 100,000
