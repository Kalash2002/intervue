# Grader notes: Two Sum II: Sorted Input

Tests cover: the sample, two elements, a pair of equal values, negative values, a pair at the two ends, a pair at the end of the array, a pair of zeros next to larger values, a position that must not pair with itself, and one large input of 200,000 values whose only pair is the last two (the worst case for checking every pair). Every input has exactly one pair, as the statement promises; the sums are checked by hand.

The naive solution tries every pair; on the large input that is quadratic and times out at the limit and at 3x. The reference uses two positions that move inwards.
