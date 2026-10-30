# Grader notes: Valid Parentheses

Tests cover: the sample, the empty string, nested and sequential pairs of every kind, crossed pairs, a closer with nothing open, an opener never closed, mismatched kinds, an odd length, and two large inputs of 400,000 characters (a fully nested balanced string, and the same with the last character changed).

The naive solution keeps deleting adjacent matched pairs until none are left; on deeply nested input that is one pass per level, so it is quadratic and times out on the large tests at the limit and at 3x. The reference uses a stack.
