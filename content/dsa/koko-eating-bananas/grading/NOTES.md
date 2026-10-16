# Grader notes: Koko Eating Bananas

The question keeps its id and title but is framed as a print queue: jobs of pages instead of piles, a printer's pages per hour instead of an eating speed. The rules are the classic ones (one job per hour, idle for the rest of an hour when a job ends early). One deliberate change: `hours` goes up to `2 * 10^9` instead of `10^9`, so that a slow rate's total hours pass the `int` range at a rate the search actually tries (with `hours <= 10^9` the halving from the largest job stays above about half the answer, where the total stays under about twice `hours`, so an `int` count would not be caught).

Tests cover: the sample (an exact fit at 6, and one hour per job at 15), one hour per job, plenty of time (rate 1, and the boundary 15 against 14 hours), a single job, rounding up of partial hours, totals past the `int` range, a large rate, and a large case: 10,000 seeded jobs of 1 to 10^9 pages under five deadlines from 10,000 to 1,000,000 hours, checked by property (the returned rate fits, the rate one slower does not) with the hours counted in `long` by the test.

Traps from the walkthrough and the tests that grade them:
- Rounding down partial hours (`pages / rate`): a scratch version fails `sample`, `oneHourPerJob`, `singleJob`, `partialHoursRoundUp`, `largestRate` and the large case.
- Counting hours in an `int`: a scratch version returns 1 instead of 2 in `hoursPastTheIntRange` (three jobs of 10^9 pages, 2 * 10^9 hours; at rate 1 the count wraps negative).

The naive solution tries rates 1, 2, 3, ... and stops at the first that fits. Measured with a cold JVM on the large test: reference 13 ms, naive still running when killed at 20 s (its answer is near 10^9, so it needs about 10^9 passes over 10,000 jobs), limit 500 ms (reference 38x under the limit, naive more than 13x past the 3x line of 1,500 ms). The reference binary searches the rate between 1 and the largest job.
