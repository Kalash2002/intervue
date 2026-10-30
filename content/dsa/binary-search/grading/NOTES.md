# Grader notes: Binary Search

Tests cover: the sample (found and absent), an empty array, a single element (equal, below, above), the first and last positions and targets outside both ends, a two-element array, extreme `int` values, every target from 0 to 18 around the values 2, 4, ..., 16, and a large case: 60,000 lookups in a million values `3i - 1,500,000` (about a third of the targets present), the array built once per test JVM.

Traps from the walkthrough and the tests that grade them: a loop that stops at `low < high` with an inclusive `high` skips the last candidate (`singleElement`, `twoElements`, `firstAndLastPositions`); comparing by subtraction overflows and searches the wrong way (`extremeIntValues`, looking up `Integer.MIN_VALUE` past a middle of 0; a scratch subtraction version fails it).

The naive solution scans from the left. Measured with a cold JVM on the large test: reference 33 ms, naive 20.0 s, limit 600 ms (reference 18x under the limit, naive 11x past the 3x line of 1,800 ms). The reference is a binary search with both ends included.
