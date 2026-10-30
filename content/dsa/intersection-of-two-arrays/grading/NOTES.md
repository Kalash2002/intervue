# Grader notes: Intersection of Two Arrays

Tests cover: the sample, disjoint lists, an empty list on either side, repeated values on both sides listed once, negative and extreme values, a value shared by single-element lists, a short list inside a long one given in no order, and a large case of two 100,000-element lists, evens against odds, with exactly three shared values (0, 100 and 199,998) so that almost every lookup misses.

The naive solution scans the second list for each value of the first. That is quadratic, so on the large test it exceeds the limit and 3x the limit. The reference puts one list in a hash set. The result must be ascending, so the answer is compared as an ordered list.
