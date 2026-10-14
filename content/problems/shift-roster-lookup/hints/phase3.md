## Level 1

Each ward has 50,000 shifts and the tests ask half a million questions. Count the shifts your lookups read from the start of the list for one question.

## Level 2

The start minutes are sorted, so a binary search finds the right shift in about sixteen steps instead of walking up to 50,000. `Roster` already keeps the sorted starts for exactly this.

## Level 3

1. Make every lookup go through the floor search on `roster.starts(ward)`.
2. Remove any loop that walks the shifts from the beginning to answer a single minute.
3. In `uncovered`, start walking at the floor index rather than at the first shift.
4. Run Phase 3 tests and compare each test's time with its limit.
