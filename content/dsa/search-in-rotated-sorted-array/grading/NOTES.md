# Grader notes: Search in Rotated Sorted Array

Tests cover: the sample (found, absent), both runs and their ends plus targets between the runs and outside every value, an array that was not rotated, a single element, two elements in both orders (the statement's edge example), every rotation of eight values with every present value and a missing neighbour of each, extreme `int` values, and a large case: 40,000 lookups cycling over eight arrays of 2^19 values with rotations 0, 1, n/2, n - 1 and four others (a quarter of the targets absent), built once per test JVM.

Trap from the walkthrough and the tests that grade it: testing the left half with a strict `nums[low] < nums[middle]` treats a one-value left half as unsorted when `low == middle`. A scratch version with `<` fails `twoElements` (`[9, 3]`, target 3 gives -1) and `everyRotationEveryTarget`.

The naive solution scans from the left. Measured with a cold JVM on the large test: reference 39 to 43 ms over three runs, naive 7.2 s, limit 600 ms (reference 14x under the limit, naive 4x past the 3x line of 1,800 ms). The reference is a single binary search that keeps the sorted half when it can hold the target.
