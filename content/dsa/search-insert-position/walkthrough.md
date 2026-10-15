# How a strong round goes: Search Insert Position

## Approach

The answer is a boundary: every element left of it is smaller than the target and every element from it onwards is not. Because the array is sorted, one comparison with the middle element tells me which side of the middle the boundary is on, so I can halve the range each time.

I keep a half-open range `[low, high)` that starts as the whole array, plus the position `high = length` as a legal answer when every element is smaller. If `sorted[middle] < target`, the boundary is after the middle and `low` becomes `middle + 1`. Otherwise the middle may itself be the boundary, so `high` becomes `middle`, not `middle - 1`. When the range is empty, `low` is the answer. Equal elements need no extra case: an element equal to the target is not smaller, so the search keeps moving left until it reaches the first of them. That is also why the same code answers "where is it" and "where would it go".

```java
static int insertAt(int[] sorted, int target) {
    int low = 0;
    int high = sorted.length; // the answer always lies in [low, high]
    while (low < high) {
        int middle = (low + high) >>> 1;
        if (sorted[middle] < target) {
            low = middle + 1; // the boundary is after the middle
        } else {
            high = middle; // the middle may itself be the boundary
        }
    }
    return low;
}
```

This question differs from its best-known version in allowing repeated values: there the elements are distinct and any position of a match is the position. Here the leftmost one is required, and the same loop gives it.

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Binary search on the boundary | O(log n) per call | O(1) |
| Scan from the left until an element is not smaller | O(n) per call | O(1) |

## Where it shows up

`shift-roster-lookup` runs this search over start minutes. `RosterLookup.lastStartAtOrBefore` calls `Collections.binarySearch` on the sorted list of a ward's shift starts, and when the minute is not a start the result is `-(insertion point) - 1`, the insertion point being the answer of this question. The shift before that insertion point (index `-found - 2`) is the last one starting at or before the minute, which is what `onDutyAt`, `nextHandover` and `uncovered` need. This question asks for the insertion point itself, with the first of several equal elements, which `Collections.binarySearch` does not promise when there are repeats.

## Follow-up answers

**How would you find the position after the last equal element instead?** Change the test to `sorted[middle] <= target` to move right. The boundary is then between elements that are at most the target and elements that are greater. Subtracting the first position from this one counts the occurrences.

**How would you count how many times a value occurs?** Run both searches: the first index with an element at least `target`, and the first index with an element greater than `target`. Their difference is the count, in O(log n).

**What if the array is too large to load, and you can only read a position at a time from disk?** The search touches only O(log n) positions, so it still works: each probe is a seek and a read of one value. Keeping the top levels of probes (the first middle, the quarters and so on) in memory removes most of the reads.
