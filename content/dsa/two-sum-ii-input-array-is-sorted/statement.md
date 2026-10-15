# Two Sum II: Sorted Input

You are given an array of whole numbers in non-decreasing order, and a target. Exactly one pair of different positions has values that add up to the target.

Implement `SortedPair.find(int[] numbers, int target)` in `SortedPair.java`. Return an array `{i, j}` with `i < j` and `numbers[i] + numbers[j] == target`. Positions count from 0. A position cannot be used twice, but two positions may hold the same value.

Use the fact that the array is sorted. Aim for a solution that reads each element only a few times and needs only a constant amount of extra memory.

## Examples

- `numbers = [2, 4, 7, 11, 15]`, `target = 18` returns `[2, 3]`: `7 + 11 = 18`.
- `numbers = [1, 4, 4, 9]`, `target = 8` returns `[1, 2]`: the two `4` are at different positions.
- `numbers = [3, 5, 7]`, `target = 10` returns `[0, 2]`: `5 + 5` would use one position twice, so it is not allowed.

## Constraints

- 2 <= `numbers.length` <= 200,000
- -1,000,000,000 <= `numbers[i]` <= 1,000,000,000, in non-decreasing order.
- Exactly one pair of positions adds up to `target`.
