# Grader notes: Container With Most Water

Tests cover: the sample, two walls, a pair with empty space between them, all walls equal, ascending and descending heights, walls of height zero, a pair of tall walls in the middle that is beaten by a wider pair of shorter ones (the trap for greedy-by-tallest solutions), and two large inputs of 200,000 walls (outer walls of 10,000 around random inner walls, so the answer is the outermost pair and sits just under the largest value an `int` can hold; and two tall walls far apart among zeros).

The naive solution measures every pair; on the large inputs that is quadratic and times out at the limit and at 3x. The reference moves the shorter wall inwards.
