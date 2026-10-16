# Grader notes: Longest Valid Parentheses

Tests cover: the three statement examples, the empty string, only openers and only closers, a balanced string in full, a stretch split by an unmatched opener (`((()(()` gives 2), a string with two balanced stretches where the first is longer, a nested-and-sequential mix (`(()(()))` gives 8), and a large case of 100,000 characters (`)` then 49,999 pairs `()` then `(`) whose answer is 99,998.

The naive solution starts at every position and extends while the depth stays non-negative, recording each point where it returns to zero. That is quadratic, so on the large test it exceeds the limit and 3x the limit. The reference uses one stack of positions.
