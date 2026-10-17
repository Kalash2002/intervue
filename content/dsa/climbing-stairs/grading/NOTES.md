# Grader notes: Climbing Stairs

Tests cover: the sample (5, 7 and 1 steps); two steps, which can be climbed two ways; every staircase from 1 to 10 steps; 30 and 35 steps; 50 steps, whose count 20,365,011,074 does not fit in an `int`, so a solution that keeps the count in an `int` fails; and 91 steps, the largest allowed, whose count is just below `Long.MAX_VALUE`.

The two large tests have a limit of 1,000 ms each (3x is 3,000 ms). The reference, a loop that keeps only the counts for the two steps below, finishes each in well under a millisecond. The naive solution is the plain recursion `countWays(n - 1) + countWays(n - 2)`, which makes about as many calls as the answer itself: tens of billions at 50 steps and around 10^19 at 91, so it times out on both, also at 3x. Its recursion is only n deep, so it times out rather than overflowing the stack. The memoised recursion of the walkthrough is O(n) like the reference and passes; it is not slower, only longer.
