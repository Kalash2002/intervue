# How a strong round goes: Contains Duplicate

## Approach

I would ask what I need to know when I reach a value: only whether I have met it before. A hash set answers that in constant time on average, so I walk the array once, add each value, and return true the moment an add reports the value was already there. If the walk finishes, nothing repeated. Comparing every pair does the same job but its cost grows with the square of the length.

```java
static boolean hasDuplicate(int[] nums) {
    Set<Integer> seen = new HashSet<>();
    for (int value : nums) {
        if (!seen.add(value)) { // add returns false when the value was already there
            return true;
        }
    }
    return false;
}
```

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Compare every pair | O(n squared) | O(1) |
| Sort, then compare neighbours | O(n log n) | O(1) to O(n), depending on the sort |
| Hash set, one pass | O(n) on average | O(n) |

## Where it shows up

`duplicate-order-detector` starts here: its first check, `exactRepeats`, asks whether the same order appears twice, which is this question once the order is turned into a key. The same set-of-seen-keys walk appears in any import or ingest step that must reject repeated records.

## Follow-up answers

**What if memory is tight and you may reorder the array?** Sort in place and compare each element with its neighbour. That is O(n log n) time and no extra set, at the price of changing the array of the caller.

**How would you report which values repeat, not just whether any does?** Keep a second set, or a map from value to count, and collect a value the first time it is seen a second time. Do not stop early.

**What if you only care about repeats that are within k positions of each other?** Keep only the last k values in the set: after adding position i, remove the value at position i minus k. Memory then stays O(k). A map from value to its last position works too.
