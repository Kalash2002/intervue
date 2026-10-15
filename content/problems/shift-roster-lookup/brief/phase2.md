## Phase 2: handovers and gaps

Implement two methods in `RosterLookup`:

```java
OptionalInt nextHandover(String ward, int minute)
List<Gap> uncovered(String ward, int fromMinute, int toMinute)
```

`nextHandover` returns the start minute of the first shift in the ward that starts **strictly after** `minute`, or empty when there is none or the ward is unknown.

`uncovered` rules:

- Return the stretches of minutes in `[fromMinute, toMinute)` that no shift of the ward covers, earliest first, as `Gap(start, end)` with the end excluded.
- Gaps are cut to the requested range. Neighbouring stretches are one gap, never split.
- An unknown ward is one gap covering the whole range.
- Throw `IllegalArgumentException` when `fromMinute` is not less than `toMinute`.

Run **Phase 2 tests**.
