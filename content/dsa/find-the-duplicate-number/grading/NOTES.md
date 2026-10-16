# Grader notes: Find the Duplicate Number

Tests cover: the sample, a value repeated four times with two values missing and a value repeated four times among five, the smallest input `[1,1]`, the duplicate being the largest value, the duplicate being 1, adjacent copies, an explicit check that the array is unchanged, an array of 200,001 values in which half the positions hold the same value, and a shuffled array of 200,001 values whose two copies are the last two positions (fixed seeds). Every test compares `nums` with a copy taken before the call.

Traps from the walkthrough and hints, and the tests that grade them:
- Writing to the array (sorting it, or marking seen values by negating): scratch versions fail every test whose array the method changes (8 of 9 and 9 of 9) with "nums was changed".
- The sum formula: a scratch version that subtracts 1 + ... + n from the sum fails `valueRepeatedSeveralTimes` and `halfTheArrayIsOneValue`.

Extra space is not graded: the walkthrough's Better versions both pass (a boolean array of seen values, 8 ms on the large test; binary search on the value, 18 ms).

The naive solution compares every pair of positions, O(n²); the large test puts the two copies last, so it reads about n²/2 pairs. Measured with a cold JVM (limit 250 ms on `largeArrayWithTheCopiesAtTheEnd`): reference 5, 4 and 5 ms over three runs (at most 31 = limit/8); naive 3.76 s when given 100x the limit (needs 1.125 s = 1.5 x 3 x limit; 5x past the 3x line of 750 ms). At 100,001 values the naive took only 0.92 s, too close, so `n` goes up to 2 * 10^5 here. The reference walks the array as links with slow and fast pointers.
