# Merge Intervals

The door log of a building records visits as closed ranges `[enter, leave]` in minutes, in no particular order. Two ranges belong together when they overlap or touch, that is, when one starts no later than the other ends. Combine every such group into one range that runs from the group's earliest start to its latest end.

Implement `Schedule.mergeRanges(int[][] ranges)` in `Schedule.java`. Return the combined ranges sorted by start, with no two of them overlapping or touching. An empty input gives an empty result.

## Example 1

**Input:** `ranges = [[14, 17], [2, 5], [4, 8]]`
**Output:** `[[2, 8], [14, 17]]`
**Explanation:** `[2, 5]` and `[4, 8]` overlap on 4 to 5, so they become `[2, 8]`. The range `[14, 17]` starts after 8 and stands alone.

## Example 2

**Input:** `ranges = [[6, 9], [1, 6]]`
**Output:** `[[1, 9]]`
**Explanation:** The ranges touch at 6, and touching ranges belong together.

## Example 3

**Input:** `ranges = [[1, 10], [3, 4], [5, 6]]`
**Output:** `[[1, 10]]`
**Explanation:** One long range contains the other two. The merged range ends at 10, not at 6, where the last range ends.

## Constraints

- `0 <= ranges.length <= 10^5`
- `0 <= enter <= leave <= 10^9` for every range

## Follow-up

What if the ranges arrive one at a time and you must keep the merged list up to date?
