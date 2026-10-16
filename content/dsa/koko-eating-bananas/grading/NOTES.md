# Grader notes: Koko Eating Bananas

The rules are the classic ones (one pile per hour, rest for the remainder of an hour when a pile runs out early), in our own words and numbers. One deliberate change, stated in the statement: `hours` goes up to `2 * 10^9` instead of `10^9`, so that a slow speed's total hours pass the `int` range at a speed the search actually tries (with `hours <= 10^9` the halving from the largest pile stays above about half the answer, where the total stays under about twice `hours`, so an `int` count would not be caught).

Tests cover: the sample (an exact fit at 6, and one hour per pile at 15), one hour per pile, plenty of time (speed 1, and the boundary 15 against 14 hours), a single pile, rounding up of partial hours, totals past the `int` range, a large speed, and a large case: 10,000 seeded piles of 1 to 10^9 bananas under five time limits from 10,000 to 1,000,000 hours, checked by property (the returned speed fits, the speed one slower does not) with the hours counted in `long` by the test.

Traps from the walkthrough and the tests that grade them:
- Rounding down partial hours (`bananas / speed`): a scratch version fails `sample`, `oneHourPerPile`, `singlePile`, `partialHoursRoundUp`, `largestSpeed` and the large case.
- Counting hours in an `int`: a scratch version returns 1 instead of 2 in `hoursPastTheIntRange` (three piles of 10^9 bananas, 2 * 10^9 hours; at speed 1 the count wraps negative).

The naive solution tries speeds 1, 2, 3, ... and stops at the first that fits. Measured with a cold JVM on the large test: reference 13 ms, naive still running when killed at 20 s (its answer is near 10^9, so it needs about 10^9 passes over 10,000 piles), limit 500 ms (reference 38x under the limit, naive more than 13x past the 3x line of 1,500 ms). The reference binary searches the speed between 1 and the largest pile.
