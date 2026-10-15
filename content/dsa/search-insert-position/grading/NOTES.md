# Grader notes: Search Insert Position

Tests cover: the sample (found at the first of two equals, between elements, before all, after all), an empty array, a single element, all elements equal, the first of several equal elements, negative values, extreme `int` values, every target from 0 to 16 around the elements 2, 4, ..., 14, and a large case: the array `0, 2, 4, ...` of a million elements searched 60,000 times with targets spread over the whole range, where the answer is `(target + 1) / 2`.

The statement allows repeated values and asks for the leftmost position, a small step beyond the best-known version where elements are distinct; the tests with duplicates check it.

The naive solution scans from the left. Measured with a cold JVM on the large test: reference 34 ms, naive 13.2 s, limit 600 ms (reference 17x under the limit, naive 7.4x past the 3x line of 1,800 ms). The reference is a binary search for the boundary.
