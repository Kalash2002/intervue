# How a strong round goes: Shift Roster Lookup

**Read this first.** This is one good way through the problem, not the only one. The complexity figures describe the reference solution. The practice links go to other sites and to different questions that use the same technique; they are not this question. The prompts are written for interview mode, where the assistant does what you specify and nothing more.

## Minutes 0 to 5: read before you run

Say aloud: "I'll read the shift, the roster and the lookup, and sketch the sample timeline, before I run anything."

1. `Shift.covers`: start included, end excluded.
2. `Roster`: shifts per ward sorted by start, a parallel list of start minutes, and an overlap check when it is built. So within a ward, shifts never overlap; they can leave gaps.
3. `RosterLookup.onDutyAt`: one `Collections.binarySearch` and an index conversion. Read the comment and the conversion together, then the end check after it.
4. `data/roster.csv`: ICU is ASHA 0 to 480, BEN 480 to 960, a gap, CARA 1020 to 1500. WARD-2 has no gaps.
5. `Phase1Tests.java`: five tests. The first is about exact start minutes, which the brief says are right.

Only now run the phase 1 tests.

## Prompt: understand the codebase fast

```text
Read-only question. For this project give me:
1. One line per class: what it owns and which other classes it calls.
2. For Collections.binarySearch(List.of(0, 480, 1020), key): the return value for keys 480,
   100 and 1000, the insertion point for each, and the index of the last start <= key.
3. Which conditions onDutyAt checks before returning a nurse.
Keep it under 150 words. Mark anything you are inferring.
```

Check item 2 yourself with the Javadoc: for a missing key, `binarySearch` returns `-(insertion point) - 1`. For 100 that is -2, insertion point 1, floor index 0.

## Phase 1: the next nurse (minutes 5 to 12)

### Reproduce

Run phase 1 tests. `theExactStartOfAShift` and `afterTheLastShiftAndInUnknownWards` pass. `theMiddleOfAShift` gets BEN at minute 100, `nobodyDuringAGap` gets CARA at minute 1000, and `beforeTheFirstShift` names ZOE before her shift starts.

### Narrow it down, aloud

"Exact starts are found, so the search is fine; only the conversion for a missing key is wrong. `-found - 1` is the insertion point, which is the first shift starting **after** the minute: the next nurse. The shift that can cover the minute is the one before it, `-found - 2`. And the end check alone cannot spot a gap or a minute before the first shift, because it never checks the start."

### The fix

```java
int index = found >= 0 ? found : -found - 2;
if (index < 0) {
    return Optional.empty();
}
Shift shift = roster.shifts(ward).get(index);
return shift.covers(minute) ? Optional.of(shift.nurse()) : Optional.empty();
```

Run phase 1 tests again; all five pass.

### Say this

"Exact starts were right because a found key returns its own index, and the conversion only runs for missing keys. The lookup is one binary search, O(log n) per question."

## Phase 2: handovers and gaps (minutes 12 to 30)

### Analysis before any prompt

- Both methods start from the same question as phase 1: the last shift starting at or before a minute. Pull that into a helper first.
- `nextHandover` is strictly after: at 480 the answer is 1020, not 480. It is the start of the shift after the floor.
- `uncovered` walks forward with a "covered up to" cursor, cuts each gap to `[from, to)`, adds the trailing gap after the last shift, and treats an unknown ward as one gap.

Work ICU from 0 to 1600 by hand: covered to 480, then 960; 1020 is later, so gap 960 to 1020; covered to 1500; the range ends at 1600, so gap 1500 to 1600.

### Decide the approach yourself

"A private helper `lastStartAtOrBefore` returning the floor index or -1. `nextHandover` takes the shift after it. `uncovered` starts its walk at the floor, not at index 0, so it touches only the shifts in the range."

### Prompt

```text
In RosterLookup, add a private helper lastStartAtOrBefore(List<Integer> starts, int minute)
that returns the index of the last start <= minute, or -1, using Collections.binarySearch
(found >= 0 ? found : -found - 2). Use it in onDutyAt. Then implement
nextHandover(ward, minute): the start at helper + 1 if that index exists, else empty.
Only these methods.
```

Then:

```text
Implement uncovered(ward, fromMinute, toMinute): throw IllegalArgumentException when
fromMinute >= toMinute. Set cursor = fromMinute. Walk the ward's shifts from
max(0, lastStartAtOrBefore(starts, fromMinute)) while a shift starts before toMinute: if the
shift starts after cursor, add Gap(cursor, shift start) and move on; then cursor =
max(cursor, shift end). After the walk, add Gap(cursor, toMinute) if cursor < toMinute.
An unknown ward has no shifts, so it returns one gap for the whole range.
```

### Check the reply for

- `-found - 2` and the -1 case in the helper. A hand-written search loop is the usual place for an off-by-one at either end.
- Strictly after in `nextHandover`: at 1020 there is no handover in ICU.
- The trailing gap after the last shift.
- A gap that touches the range start is cut there, not split: `uncovered("ICU", 970, 1010)` is one gap, 970 to 1010.

Run phase 2 tests after each method, then phase 1 again: `onDutyAt` now uses the helper.

## Phase 3: every ward, all week (minutes 30 to 38)

### Read the tests first

Five wards with 50,000 shifts each, and half a million lookups per test. Say how many shifts each lookup touches: "With binary search about 17, log of 50,000. A loop over the shifts is 50,000 per query, so 25 billion steps."

### What to do

If every lookup goes through the binary-search helper, phase 3 already passes. If it times out, find the loop over a ward's shifts (often in `nextHandover`, or `uncovered` starting at index 0) and route it through the helper. Run all three phases.

## The technique: binary search for the floor

**The idea.** In a sorted list, binary search finds not just exact keys but the position a missing key would take. The element just before that position is the **floor**, the largest value at or below the key. For non-overlapping intervals sorted by start, the floor interval is the only one that can contain a point; check its end and you have the answer in O(log n).

**How to recognise it.** "Which interval contains this point", "who was on duty at", "price in effect at time t", "latest version before", time-based key-value stores.

**The two decisions that cause bugs.** Floor or ceiling: `-found - 1` is the insertion point (ceiling), `-found - 2` is the floor. And the floor exists but may not cover the point: always check the end, and treat -1 as "before everything".

**In Java.** `Collections.binarySearch` and `Arrays.binarySearch` encode a missing key as `-(insertion point) - 1`. A `TreeMap<Integer, Shift>` gives the same answer with `floorEntry(minute)` and `higherKey(minute)` and needs no index arithmetic.

| Approach | onDutyAt for n shifts | uncovered over a range with k shifts |
| --- | --- | --- |
| Scan every shift | O(n) | O(n) |
| Binary search for the floor | O(log n) | O(log n + k) |
| `TreeMap` floor and higher | O(log n) | O(log n + k) |

**Practise the technique**

These open inside the app under DSA.

- [Search Insert Position](dsa://search-insert-position): the insertion point itself.
- [Time Based Key-Value Store](dsa://time-based-key-value-store): the floor of a timestamp, the closest match to `onDutyAt`.
- [Insert Interval](dsa://insert-interval): walking sorted intervals with a cursor, as in `uncovered`.

## Time budget

| Minutes | What |
| --- | --- |
| 0 to 5 | Read the shift, roster and lookup, sketch the ICU timeline |
| 5 to 12 | Phase 1: reproduce, decode `binarySearch`, floor index and `covers` |
| 12 to 16 | Phase 2 analysis aloud and ICU gaps by hand |
| 16 to 30 | Phase 2: helper, `nextHandover`, `uncovered`, a run after each |
| 30 to 38 | Phase 3: read the tests, count shifts per lookup, run all three phases |
| 38 to 45 | Read your whole diff, answer the follow-ups aloud |

## Follow-up answers

**What does binarySearch return for a missing key, and why?** `-(insertion point) - 1`, where the insertion point is the index of the first element greater than the key. The extra minus one makes every "not found" result negative, including insertion point 0, so one comparison with 0 tells found from not found.

**Why were exact start minutes right?** A found key returns its own index, and the broken conversion only runs for missing keys.

**Cost.** `onDutyAt` and `nextHandover` are O(log n) for n shifts. `uncovered` is O(log n + k) where k is the number of shifts inside the range.

**Overlapping handovers.** The floor shift may have ended while an earlier, longer one still covers the minute, and two nurses can be on duty at once. The roster's overlap check must go, and the lookup must return every shift that covers the minute: an interval tree, or for short overlaps, check the floor and a few shifts before it. The app should show both nurses during the handover, marked as outgoing and incoming.

**What the assistant got wrong.** Answer from your own session. Typical: a hand-written search loop off by one at the ends, the floor returned without checking its end, `>=` in `nextHandover`, a gap split at the range start, or no trailing gap.
