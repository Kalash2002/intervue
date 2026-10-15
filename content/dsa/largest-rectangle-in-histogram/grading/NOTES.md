# Grader notes: Largest Rectangle in Histogram

Tests cover: the sample (6), an empty skyline, one bar, all-zero bars, a flat skyline (6), rising bars (`[1, 2, 3, 4, 5]` returns 9, found only when the leftover stack is flushed after the last bar), falling bars (9), a valley where a wide rectangle beats a tall one (12), a short bar that spans everything (`[4, 9, 5, 6]` returns 16; a wrong left boundary after an empty stack fails here), repeated heights, and two large inputs of 100,000 bars (a flat skyline of height 10,000, where the answer is 1,000,000,000, and a staircase falling from 10,000 in steps every ten bars, where the answer is 250,050,000).

The naive solution extends right from every bar tracking the lowest height; on both large inputs that is about 5 billion steps, which times out at the limit and at 3x. The reference uses a stack.
