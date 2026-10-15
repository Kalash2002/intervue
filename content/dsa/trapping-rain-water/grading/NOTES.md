# Grader notes: Trapping Rain Water

Tests cover: the sample, an empty row and rows of one or two bars, heights that only rise or only fall, a flat row, a simple basin, a single dip, two basins of different depth (hand-computed as 11), a row with plateaus (11), a tall wall in the middle with lower sides (7), and two large inputs of 100,000 bars (a deep basin between two walls, and walls alternating with gaps). Both large answers are close to the largest value an `int` can hold in this problem (about 1e9 and 5e8), which fits.

The naive solution scans left and right for the maxima of every position; on the large inputs that is quadratic and times out at the limit and at 3x. The reference works in from both ends.
