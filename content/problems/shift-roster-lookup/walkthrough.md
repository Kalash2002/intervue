# How a strong round goes: Shift Roster Lookup

**Read this first.** This is one good way through the problem, not the only one. The prompts are written for interview mode, where the assistant does what you specify and nothing more.

## Minutes 0 to 5: read before you run

1. `Shift.covers`: start included, end excluded.
2. `Roster`: shifts per ward sorted by start, a parallel list of start minutes, and an overlap check.
3. `RosterLookup.onDutyAt`: one `binarySearch` and an index conversion. Read the comment and the conversion together.
4. `data/roster.csv`: the ICU has a gap from 960 to 1020.

## Phase 1: the next nurse (minutes 5 to 12)

Run phase 1. Exact starts pass; minute 100 returns BEN. Say: "`binarySearch` returns minus the insertion point minus one. The insertion point is the next shift; the shift that can cover the minute is the one before it."

```text
Read-only question. For Collections.binarySearch([0, 480, 1020], 100), what is returned,
what is the insertion point, and which index is the last element less than or equal to 100?
```

Fix with `-found - 2`, handle -1, and check `covers` so gaps return nobody.

## Phase 2: handovers and gaps (minutes 12 to 30)

Extract a `lastStartAtOrBefore` helper. `nextHandover` is the helper plus one. For `uncovered`, walk forward with a cursor; work the ICU from 0 to 1600 by hand first: gap 960 to 1020, then 1500 to 1600.

```text
Using a private helper lastStartAtOrBefore(starts, minute) that returns the floor index or -1,
implement nextHandover (the start after the floor, strictly later) and uncovered(ward, from, to)
as a forward walk from max(0, floor) with a covered-up-to cursor, cutting gaps to [from, to).
Throw IllegalArgumentException when from >= to. Unknown wards have no shifts.
```

Check the reply for the strict "after" in `nextHandover` and the final trailing gap.

## Phase 3: every ward, all week (minutes 30 to 38)

With binary search everywhere, phase 3 passes. A loop over the shifts per query times out.

## Complexity

O(log n) per lookup; `uncovered` is O(log n + shifts in the range).

## Follow-up answers

- `-(insertion point) - 1` is negative for every insertion point, including 0, so callers can tell "found" from "not found" with one comparison.
- With overlapping handover periods, the floor shift may have ended while an earlier one still covers the minute; keep shifts in an interval tree or show both nurses during the overlap.
