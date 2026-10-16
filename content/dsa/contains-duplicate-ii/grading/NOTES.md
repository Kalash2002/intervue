# Grader notes: Contains Duplicate II

Tests cover: the sample both ways, no codes, one code, a gap of 0, adjacent equal codes with a gap of 1, a repeat that is too far apart for the gap and exactly at the gap, extreme and negative values, a code seen three times where only the latest pair is close enough (this catches a map that keeps the first position), and a large case of 100,000 distinct codes with a gap of 99,999, where the answer is false and every position has to be checked.

The naive solution compares each position with the next `gap` positions. That is O(n times gap), so on the large test it exceeds the limit and 3x the limit. The reference keeps a hash set of the last `gap` codes (O(min(n, gap)) extra space); the map from code to last position is the Better approach.
