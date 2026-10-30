# Grader notes: Insert Interval

Tests cover: the sample, a touching merge, an empty list, a new range before everything, after everything, in a gap between two ranges, inside an existing range, covering every range, touching only the first range from the left, a single-point range, and that the input list is left unchanged.

No naive solution: time is not graded separately for this question (ADR 0008). Appending the new range, sorting and merging is correct but only costs a logarithmic factor more, not a different order of growth worth separating by timing.
