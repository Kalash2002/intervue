# Time Based Key-Value Store

Build a small store that remembers every value a key has had over time and can answer what a key held at a given moment.

Implement the class `TimeMap` in `TimeMap.java` with two methods:

- `void set(String key, String value, int timestamp)` records that `key` took `value` at `timestamp`.
- `String get(String key, int timestamp)` returns the value recorded for `key` with the largest timestamp that is less than or equal to `timestamp`. If the key has no such record, return the empty string `""`.

For a given key, calls to `set` always arrive with strictly increasing timestamps.

## Examples

- After `set("door", "locked", 10)` and `set("door", "open", 20)`: `get("door", 15)` returns `"locked"`, `get("door", 20)` returns `"open"`, and `get("door", 5)` returns `""`.
- `get("window", 100)` on a key that was never set returns `""`.
- After `set("a", "x", 1)` and `set("b", "y", 2)`: `get("a", 50)` returns `"x"`; keys do not affect each other.

## Constraints

- Up to 300,000 calls to `set` and 300,000 calls to `get` in one run.
- 0 <= `timestamp` <= 1,000,000,000
- Keys and values are non-empty strings.
