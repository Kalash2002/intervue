# Longest Consecutive Sequence

You are given an unsorted array of whole numbers. A consecutive run is a group of values that, put in order, each go up by exactly one, such as `7, 8, 9`. The values do not have to sit next to each other in the array, and a value that appears several times counts once.

Implement `Streaks.longest(int[] nums)` in `Streaks.java`. Return the length of the longest consecutive run that can be formed from the values of `nums`. Return `0` for an empty array.

## Examples

- `nums = [41, 7, 40, 39, 8, 38, 9, 500]` returns `4`: the values `38, 39, 40, 41` form the longest run, and `7, 8, 9` is shorter.
- `nums = [3, 4, 4, 5, 5, 5, 6]` returns `4`: the repeats count once, leaving the run `3, 4, 5, 6`.
- `nums = [10, 20, 30]` returns `1`: no two values are neighbours.

## Constraints

- 0 <= `nums.length` <= 100,000
- -1,000,000,000 <= `nums[i]` <= 1,000,000,000
