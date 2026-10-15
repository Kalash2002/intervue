# Grader notes: Shift Roster Lookup

**Phase 1 bug.** `onDutyAt` converts a negative `binarySearch` result with `-found - 1`, which is the insertion point: the first shift starting after the minute. The floor is `-found - 2`. Combined with checking only `minute < end`, mid-shift minutes return the next nurse and gap minutes return the shift after the gap. Exact start minutes return a non-negative index and are right, which is the clue in the brief. Fix: `-found - 2`, treat -1 as empty, and check `covers`.

**Phase 2.** Reusing a floor helper is the clean answer. `nextHandover` is strictly after the minute: at 480 the answer is 1020, not 480. `uncovered` walks forward with a "covered up to" cursor, cuts gaps to the range, and treats an unknown ward as one gap. Starting the walk at index 0 is correct but slower; starting at the floor index is the intended shape.

**Phase 3.** 50,000 shifts per ward and half a million queries per test. Linear scans cost about 1.25 x 10^10 steps and time out; binary search costs about 17 per query.

**Common assistant mistakes to watch for.** Replacing `binarySearch` with a hand-written loop that is off by one at the ends; returning the floor shift without checking its end (a gap minute returns the previous nurse); `>=` in `nextHandover`; splitting a gap at the range start; forgetting the trailing gap after the last shift.
