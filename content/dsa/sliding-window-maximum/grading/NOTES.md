# Grader notes: Sliding Window Maximum

Tests cover: the sample, a window of one, a window as wide as the array, all values equal, a strictly increasing and a strictly decreasing array (the two cases where the deque keeps one element or all of them), negative values, a maximum that leaves the window exactly when it expires, and a large array of 1,000,000 seeded random values with a window of 500,000, checked against a monotone reference computed inside the test with prefix and suffix maxima.

The naive solution rescans every window, so it does about k times n operations and times out on the large test at the limit and at 3x. The reference uses a deque of positions.
