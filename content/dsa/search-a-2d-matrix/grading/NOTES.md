# Grader notes: Search a 2D Matrix

Tests cover: the sample (found and absent), a single cell, a single column (the statement's edge example), a single row, the four corners and targets outside both ends, targets in the gap between one row's end and the next row's start, a wide (2 by 5) and a tall (5 by 2) grid probed with every value from 0 to 11, values at the constraint limits, and a large case: 60,000 lookups in a 1,000 by 1,000 grid holding `3k - 1,500,000` at flat position k (about a third of the targets present), the grid built once per test JVM.

Trap from the walkthrough and the tests that grade it: converting a flat position with the row count instead of the row length. A scratch version doing that fails `singleColumn`, `singleRow`, `corners`, `wideAndTallGridsFindEveryValue` and `negativeAndLimitValues` (wrong answers or out-of-bounds reads); square grids alone would not catch it.

The naive solution looks at every cell. Measured with a cold JVM on the large test: reference 29 ms (one noisy first run of 63 ms), naive 15.3 s, limit 600 ms (reference 20x under the limit at its usual time, naive 8.5x past the 3x line of 1,800 ms). The reference is a binary search over flat positions.
