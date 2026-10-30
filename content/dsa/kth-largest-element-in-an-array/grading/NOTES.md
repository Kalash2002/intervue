# Grader notes: Kth Largest Element in an Array

Tests cover: the sample (19), repeats that count separately (`8, 3, 8, 8, 1` gives 8 for `k = 3` and 3 for `k = 4`; a version that removes repeats with a `TreeSet` returns 1 and fails), `k` equal to the length, `k = 1`, a single value, values at the limits, a small all-equal array, and three large tests of 100,000 values with a 1,000 ms limit each:

- `largeShuffled`: the values `2i - 100,000` for `i` = 0..99,999, shuffled with a fixed seed, so the k-th largest is `2 * (100,000 - k) - 100,000`; asked for `k` = 1, 31,415, 50,000 and 100,000.
- `largeAllEqual`: 100,000 copies of 424,242. A quickselect whose partition sends every value equal to the pivot to the same side (the textbook two-part Lomuto split) shrinks the range by one per round, O(n²); a scratch version with a random pivot timed out here.
- `largeSortedAndRepeating`: the sorted values -50,000..49,999 (the k-th largest is `50,000 - k`), and the values `i % 1000` in cycling order, each of 0..999 a hundred times (the k-th largest is `999 - (k - 1) / 100`). A quickselect that always takes the last value as its pivot is quadratic on the sorted array; a scratch version timed out here.

No naive solution: sorting is only a log factor slower than quickselect at this size (63 ms against 13 ms on `largeShuffled`), too small a gap to time reliably, so time is not graded separately for this question (ADR 0008). The limits on the large tests still catch the quadratic quickselects above. Sorting, the size-k heap and the reference's three-way quickselect with a random pivot all pass.
