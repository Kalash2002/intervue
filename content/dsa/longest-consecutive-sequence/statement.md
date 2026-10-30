# Longest Consecutive Sequence

You are given an unsorted array of whole numbers. A consecutive run is a group of values that, put in order, each go up by exactly one, such as `7, 8, 9`. The values do not have to sit next to each other in the array, and a value that appears several times counts once.

Implement `Streaks.longest(int[] nums)` in `Streaks.java`. Return the length of the longest consecutive run that can be formed from the values of `nums`. Return `0` for an empty array.

## Example 1

**Input:** `nums = [41, 7, 40, 39, 8, 38, 9, 500]`
**Output:** `4`
**Explanation:** The values `38, 39, 40, 41` form a run of length 4. The values `7, 8, 9` form a shorter run of 3, and 500 is alone.

## Example 2

**Input:** `nums = [3, 4, 4, 5, 5, 5, 6]`
**Output:** `4`
**Explanation:** The repeats count once, which leaves the values 3, 4, 5 and 6 and the run `3, 4, 5, 6`. Counting every copy would wrongly give 7.

## Example 3

**Input:** `nums = []`
**Output:** `0`
**Explanation:** An empty array holds no values, so there is no run at all.

## Constraints

- `0 <= nums.length <= 10^5`
- `-10^9 <= nums[i] <= 10^9`

## Follow-up

Can you do it in O(n) time?
