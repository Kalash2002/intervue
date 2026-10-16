# Grader notes: Find Minimum in Rotated Sorted Array

Tests cover: the sample (minimum in the middle, minimum last), arrays that were not rotated, a single element, two elements in both orders, every rotation of three values, every rotation of eight values, extreme `int` values, and a large case: 40,000 calls cycling over eight arrays of 2^19 values with rotations 0, 1, n/2, n - 1 and four others, built once per test JVM.

Traps from the walkthrough and the tests that grade them:
- Comparing the middle with the first value instead of the last: a scratch version returns the largest value of an unrotated array and fails `notRotated`, `twoElements`, `threeElementsEveryRotation`, `everyRotationOfEightValues` and the large case.
- Moving `high` to `middle - 1` when the middle is in the lower run loses the minimum: a scratch version fails `threeElementsEveryRotation` (`[5, 1, 3]` gives 5), `everyRotationOfEightValues` and the large case.

The naive solution scans every value. Measured with a cold JVM on the large test: reference 15 ms, naive 7.6 s, limit 400 ms (reference 27x under the limit, naive 6.3x past the 3x line of 1,200 ms). The reference is a binary search against the last value of the range.
