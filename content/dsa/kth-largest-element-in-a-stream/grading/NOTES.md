# Grader notes: Kth Largest Element in a Stream

Tests drive a `KthLargest` through a constructor call and a run of `add` calls and check every returned value. They cover: the sample, repeated values that must count separately (`k = 3` with 7, 7, 7, 12, 12, 15; a version that keeps a `TreeSet` drops the repeats and returns 7 instead of 12 on the last call), a board that starts empty with `k = 1`, `k` equal to the number of values, values below the k-th that leave it unchanged next to values above it that move it, values at the limits of the range, two boards that must not share state (a version with static fields fails), and a large run.

The large run starts from a shuffled 0..99,999 with `k = 50,000` and makes 100,000 `add` calls that alternate a new highest value with a value far below the k-th. Each new high moves the k-th largest up by exactly one, so after call `i` (from 0) the answer is `50,001 + i / 2`; the test computes that itself.

The naive solution appends each value and sorts a copy of all values on every `add`, O(n log n) per call. On the large run that is 100,000 sorts of over 100,000 values, which times out at the limit and at 3x. The reference keeps a min-heap of the k largest values. The walkthrough's Better, a sorted array of the k largest, shifts up to `k` values per `add`; it took 310 ms on the large run (reference 52 ms) and passes.
