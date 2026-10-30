# Grader notes: Valid Anagram

Tests cover: the sample, two empty strings, different lengths, same length with different counts (the trap for solutions that compare only which letters appear), repeated letters, single letters, identical words, disjoint letters, and two large inputs of 200,000 letters (two long runs of one letter, and a shuffled copy that is then changed in one letter).

The naive solution matches each letter of the first word to an unused equal letter in the second by scanning from the start; on long runs of one letter that is quadratic and times out on the large tests at the limit and at 3x. The reference counts letters in a table of 26.
