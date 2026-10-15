# How a strong round goes: Contains Duplicate II

## Approach

For the code at position `j`, the only earlier position that can matter is the nearest earlier one with the same code. If that one is more than `gap` places back, every older one is further still. So per code I only need to remember the last position where I saw it.

I walk the list once with a map from code to its last position. At each position I look the code up: if it is present and `j - last <= gap`, I return `true`. Whether or not it matched, I then store the current position as the last one, so the next occurrence compares with the closest one. If I reach the end, the answer is `false`.

```java
static boolean repeatsNearby(int[] codes, int gap) {
    Map<Integer, Integer> lastSeen = new HashMap<>();
    for (int j = 0; j < codes.length; j++) {
        Integer last = lastSeen.get(codes[j]);
        if (last != null && j - last <= gap) {
            return true;
        }
        lastSeen.put(codes[j], j); // always the latest, so the next match is the closest
    }
    return false;
}
```

A sliding window of the last `gap` codes in a set also works: add the new code, drop the one that fell out of range, and a failed add means a repeat. Both are linear; the map is the one that also tells you where the match was.

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Map from code to last position | O(n) | O(n) |
| Set holding the last `gap` codes | O(n) | O(min(n, gap)) |
| Compare each position with the next `gap` positions | O(n times gap) | O(1) |

## Where it shows up

`duplicate-order-detector` has `suspectedDuplicates`: an order is flagged when its key was last seen fewer than `windowMinutes` minutes earlier (strictly fewer: a gap of exactly the window is not flagged), so the comparison is on order minutes, not on positions in the log. That is this question, with the key built from several fields instead of one code. The map from key to last minute is the structure to reach for, and updating it on every step is the detail that makes the matches the closest ones.

## Follow-up answers

**How would you do it keeping only the last gap values in memory?** Use a set as a window. Add each code; when the window holds more than `gap` codes, remove the code that left it, which is `codes[j - gap]`. If adding a code finds it already present, that is a repeat within range.

**How would you return the closest pair of equal positions instead of a yes or no?** Keep the map of last positions and a best distance. At each position where the code is present, compare `j - last` with the best so far, then store `j`. The smallest distance ever seen is the answer.

**What changes if two values count as repeats when they differ by at most a small amount?** A plain map no longer finds neighbours. Bucket the values by their size divided by the allowed difference plus one; then a match can only be in the same bucket or the two next to it, and each bucket holds the latest value inside the window.
