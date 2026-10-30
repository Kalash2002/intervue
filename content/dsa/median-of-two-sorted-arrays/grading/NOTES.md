# Grader notes: Median of Two Sorted Arrays

Tests cover: the sample (odd and even totals), one array empty (either side, odd and even), averages ending in `.5`, extreme `int` values (two maxima, two minima, a minimum with a maximum, and a sum past the `int` range), one array entirely before the other (in both argument orders, and two halves of equal length), repeated values within and across the arrays, interleaved negative values, and a large case: 2,000 calls cycling over five pairs of arrays up to a million values each, built once per test JVM, whose medians follow from how they are built (evens and odds, one array empty, a single maximum against a million values, every value of one array above the other, a block of 400,000 equal values holding both middle positions).

Traps from the walkthrough and the tests that grade them:
- Adding the two middle values as `int`s: a scratch version returns -1.0 instead of 2147483647.0 in `extremeValuesDoNotOverflow`.
- Integer division of the sum: a scratch version fails `sample` (3.0 for 3.5), `averageOfTwoHalvesIsNotTruncated`, `extremeValuesDoNotOverflow`, `oneArrayEntirelyBeforeTheOther` and the large case.
- Empty arrays and cuts that take nothing or everything from one array: `oneArrayEmpty`, `oneArrayEntirelyBeforeTheOther` and large pairs 1, 2 and 3.

The statement asks for every call to avoid reading most of the values, so the merge (the walkthrough's Better) is meant to fail the large case: a scratch merge passes every small test and takes 1.7 s on the large one, over 4x the limit.

The naive solution copies both arrays into one and sorts it. Measured with a cold JVM on the large test: reference 6 ms, naive still running when killed at 8 s (each call sorts up to two million values), limit 400 ms (reference 67x under the limit, naive more than 4x past the 3x line of 1,200 ms). The reference binary searches the cut in the shorter array.
