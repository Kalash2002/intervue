# Grader notes: Merge Intervals

Tests cover: the sample, touching ranges, an empty input, a single range, one range containing several others, an unsorted chain that merges into one, separate ranges given out of order, identical ranges, equal starts with different ends, and a large case of 100,000 separate ranges in shuffled order.

The naive solution repeatedly compares every pair of ranges and merges any pair that overlaps, until a full pass changes nothing. With nothing to merge, a single pass already compares about five billion pairs, so it exceeds the limit and 3x the limit. The reference sorts once and merges in a single sweep.
