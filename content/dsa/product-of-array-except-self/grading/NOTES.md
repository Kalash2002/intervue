# Grader notes: Product of Array Except Self

Tests cover: the sample, two elements, negative numbers, one zero, two zeros, all zeros, a zero at either end, all ones, and two large inputs of 200,000 numbers (only 1 and -1 with twenty 2s, one without and one with a single zero). The zero cases are the trap for solutions that divide the total product by each number.

The naive solution multiplies the other numbers again for every position; on the large inputs that is quadratic and times out at the limit and at 3x. The reference sweeps from both ends.
