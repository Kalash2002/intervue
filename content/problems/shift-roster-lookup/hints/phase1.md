## Level 1

Exact start minutes work and every other minute is off by one shift. Read what `Collections.binarySearch` returns when the minute is not in the list, then follow how `onDutyAt` turns that into an index.

## Level 2

For a missing key, `binarySearch` returns minus the insertion point, minus one. The insertion point is the index of the first start that is later than the minute, so the shift that could cover the minute is the one just before it. The code uses the insertion point itself, and only checks the end minute, so it lands on the next shift.

## Level 3

1. Open `RosterLookup.onDutyAt`.
2. For a negative result, take one less than the insertion point: the last shift starting at or before the minute.
3. An index of -1 then means the minute is before the first shift; return empty for it.
4. Check that the shift really covers the minute, start and end, rather than only the end.
5. Run Phase 1 tests: minute 1000 in the ICU should have nobody on duty.
