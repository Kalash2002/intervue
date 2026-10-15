# Grader notes: Longest Consecutive Sequence

Tests cover: the sample, an empty array, a single value, repeated values (the trap for solutions that count each copy), negative values across zero, no neighbours at all, runs with a gap just after or before them, a fully reversed run, values at the edges of the allowed range, and two large inputs of 100,000 numbers (a shuffled single run, and the same run with every value doubled).

The naive solution starts a walk from every value and goes up while the next value is present, without checking for the start of a run; on one long run that is quadratic and times out on the large tests at the limit and at 3x. The reference starts only at run beginnings.
