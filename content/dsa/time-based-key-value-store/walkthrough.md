# How a strong round goes: Time Based Key-Value Store

## Approach

Each key gets its own history, and a history is only ever appended to with larger timestamps, so it is already sorted. A lookup "latest value at or before time t" is then a search for the rightmost entry whose timestamp is at most t, and binary search finds it in O(log n). I keep a hash map from key to a history of two parallel lists. In the search I remember every index that qualifies and keep moving right, so the answer is the last one that qualified, and "none qualified" gives the empty string. Scanning the history for every get works but is linear per call.

```java
String get(String key, int timestamp) {
    History history = histories.get(key);
    if (history == null) {
        return "";
    }
    int low = 0;
    int high = history.times.size() - 1;
    int best = -1; // rightmost index whose time is at most the query
    while (low <= high) {
        int middle = (low + high) >>> 1;
        if (history.times.get(middle) <= timestamp) {
            best = middle;
            low = middle + 1;
        } else {
            high = middle - 1;
        }
    }
    return best < 0 ? "" : history.values.get(best);
}
```

## Complexity

With n records for one key:

| Approach | `set` | `get` | Extra space |
| --- | --- | --- | --- |
| Scan the history | O(1) | O(n) | O(n) |
| Sorted lists and binary search | O(1) | O(log n) | O(n) |

## Where it shows up

`shift-roster-lookup` asks who was on duty at a given minute, `onDutyAt`. The shifts are sorted by start, and the answer is the last shift that started at or before the minute, with a check that it has not already ended. That is the "floor" search of this question. The same lookup answers "price in effect at time t" and "which version was live when the error happened".

## Follow-up answers

**What if timestamps for a key may be set out of order?** Appending no longer keeps the list sorted. Use a sorted structure per key, such as a `TreeMap` from timestamp to value with `floorEntry`, at O(log n) per set and per get. Or keep the append-only list and sort it lazily before the first get after a change.

**How would you delete or expire old values?** Records older than the retention time can be dropped from the front of each history, as the list is sorted. With parallel lists, store a start offset instead of removing from the front of an array list, and compact now and then.

**What if you also need the first value after a given time?** Binary search for the leftmost index whose timestamp is above the query instead, or take the floor index plus one. A `TreeMap` offers `higherEntry` for the same thing.
