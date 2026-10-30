# Time Based Key-Value Store

Build a small store that remembers every value a key has had over time and can answer what a key held at a given moment.

Implement the class `TimeMap` in `TimeMap.java` with two methods:

- `void set(String key, String value, int timestamp)` records that `key` took `value` at `timestamp`.
- `String get(String key, int timestamp)` returns the value recorded for `key` with the largest timestamp that is less than or equal to `timestamp`. If the key has no such record, return the empty string `""`.

## Example 1

**Input:** `set("door", "locked", 10)`, `set("door", "open", 20)`, `set("door", "locked", 45)`, `set("door", "open", 70)`, then `get("door", 50)`, `get("door", 20)` and `get("door", 5)`
**Output:** `"locked"`, `"open"` and `""`
**Explanation:** At time 50 the latest record at or before it is the one at 45, which holds `"locked"`. Time 20 matches a record exactly, so its value `"open"` counts. Time 5 is before the first record, so there is nothing to return.

## Example 2

**Input:** `set("a", "x", 1)`, `set("b", "y", 2)`, then `get("a", 50)` and `get("c", 50)`
**Output:** `"x"` and `""`
**Explanation:** Keys do not affect each other, so `"a"` still holds `"x"` at time 50. The key `"c"` was never set, so it has no record.

## Example 3

**Input:** `set("k", "start", 0)`, then `get("k", 0)` and `get("k", 1000000000)`
**Output:** `"start"` and `"start"`
**Explanation:** Timestamp 0 is allowed, and a query at the largest possible time still returns the last record there is.

## Constraints

- `0 <= number of set calls <= 3 * 10^5`
- `0 <= number of get calls <= 3 * 10^5`
- `0 <= timestamp <= 10^9`
- `1 <= key.length <= 20`
- `1 <= value.length <= 20`
- For each key, the timestamps passed to `set` are strictly increasing.

## Follow-up

What if timestamps for a key may be set out of order?
