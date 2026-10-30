# Grader notes: Two Sum

Tests cover: a basic pair, equal values forming the pair (non-adjacent copies, `[7, 2, 7]`), negative numbers (including an all-negative array whose pair is the first two positions), a zero pair, a value that must not pair with itself (target is twice an element that appears once), the pair at the very end, values near the constraint limits, and a large array (300,000 values, the pair is the last two positions). The two positions are accepted in either order.

The naive solution checks every pair and times out on the large test at the limit and at 3x. The reference uses a hash map and finishes in milliseconds.
