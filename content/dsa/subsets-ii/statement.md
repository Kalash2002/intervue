# Subsets II

You are given an array of whole numbers that may hold the same value more than once. A subset is a selection of some of its positions, from none to all; but two subsets are the same when they hold the same values the same number of times, whichever positions those values came from. So in `[6, 6, 1]` either 6 alone gives the one subset `[6]`.

Implement `SubsetsWithRepeats.all(int[] nums)` in `SubsetsWithRepeats.java`. Return every distinct subset of `nums` exactly once, each as a list of its values. The empty subset counts. The values inside a subset may be in any order, and the subsets may be returned in any order.

## Example 1

**Input:** `nums = [3, 1, 3]`
**Output:** `[[], [1], [3], [1, 3], [3, 3], [1, 3, 3]]` (in any order)
**Explanation:** There are 8 choices of positions, but taking the first 3 or the second 3 gives the same subset, so only 6 are distinct.

## Example 2

**Input:** `nums = [2, 7, 2, 7]`
**Output:** `[[], [2], [7], [2, 2], [2, 7], [7, 7], [2, 2, 7], [2, 7, 7], [2, 2, 7, 7]]` (in any order)
**Explanation:** A subset is fixed by how many 2s (0, 1 or 2) and how many 7s (0, 1 or 2) it takes: 3 × 3 = 9 subsets.

## Example 3

**Input:** `nums = [-8, -8]`
**Output:** `[[], [-8], [-8, -8]]`
**Explanation:** There are four choices of positions, but either -8 alone is the same subset, so only three are distinct.

## Constraints

- `1 <= nums.length <= 24`
- `-10 <= nums[i] <= 10`
- The answer holds at most 5,000 subsets.

## Follow-up

Can the running time depend on the number of distinct subsets rather than on 2^n?
