# Grader notes: Two Sum

Tests cover: a basic pair, equal values forming the pair, negative numbers, a zero pair, the pair at the start and at the end, a value that must not pair with itself (target is twice an element that appears once), and a large array (300,000 values, the pair is the last two positions).

The naive solution checks every pair and times out on the large test at the limit and at 3x. The reference uses a hash map and finishes in milliseconds.
