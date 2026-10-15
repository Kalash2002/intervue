# Merge Intervals

The door log of a building records visits as closed ranges `[enter, leave]` in minutes, in no particular order. Two ranges belong together when they overlap or touch, that is, when one starts no later than the other ends. Combine every such group into a single range that runs from the group's earliest start to its latest end.

Implement `Schedule.mergeRanges(int[][] ranges)` in `Schedule.java`. Return the combined ranges sorted by start, with no two of them overlapping or touching. An empty input gives an empty result.

## Examples

- `ranges = {{14,17},{2,5},{4,8}}` returns `{{2,8},{14,17}}`: the first range is apart from the others, and the second and third overlap.
- `ranges = {{6,9},{1,6}}` returns `{{1,9}}`: they touch at 6, so they merge.
- `ranges = {{3,4}}` returns `{{3,4}}`.

## Constraints

- 0 <= `ranges.length` <= 100,000
- 0 <= enter <= leave <= 1,000,000,000 for every range.
