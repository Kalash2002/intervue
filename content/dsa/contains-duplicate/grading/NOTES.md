# Grader notes: Contains Duplicate

Tests cover: a repeat in the middle, all distinct, empty array, a single element, a repeat of adjacent values, a repeat at the first and last positions, negative values and zero, `Integer.MIN_VALUE` and `MAX_VALUE`, and a large array of 300,000 distinct values (shuffled, seeded).

The naive solution compares every pair and times out on the large test at the limit and at 3x. The reference uses a hash set.
