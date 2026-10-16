# Grader notes: Largest Rectangle in Histogram

Tests cover: the sample (6), an empty skyline, one bar, all-zero bars, a flat skyline (6), rising bars (`[2, 3, 5, 6, 8]` returns 15, found only when the leftover stack is flushed after the last bar), falling bars (`[9, 7, 4, 3, 1]` returns 14), a valley where a wide block of medium bars beats a tall one (`[5, 2, 6, 5, 6, 1, 4]` returns 15 from the three bars 6, 5, 6 at height 5), a short bar that spans everything (`[7, 12, 8, 9]` returns 28; a wrong left boundary after an empty stack fails here), repeated heights, and two large inputs of 100,000 bars (a flat skyline of height 10,000, where the answer is 1,000,000,000, and a staircase falling from 10,000 in steps every ten bars, where the answer is 250,050,000).

The naive solution extends right from every bar tracking the lowest height; on both large inputs that is about 5 billion steps, which times out at the limit and at 3x. The reference uses a stack.
