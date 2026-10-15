# How a strong round goes: Merge Intervals

## Approach

Comparing every range with every other is the obvious start, but groups chain: range A may touch B, and B touch C, while A and C are far apart. The way to avoid chasing chains is to sort by start. Then a range can only join the group that is currently open, never a group closed earlier, because everything closed ends before this range starts. I keep one open range. If the next range starts no later than the open one ends, they belong together and I extend the end to the larger of the two. Otherwise I close the open range and begin a new one. Touching counts as overlapping, so the test is less-or-equal. I copy each range before widening it, so the caller's array is not changed.

```java
static int[][] mergeRanges(int[][] ranges) {
    int[][] sorted = ranges.clone();
    Arrays.sort(sorted, (a, b) -> Integer.compare(a[0], b[0]));
    List<int[]> merged = new ArrayList<>();
    for (int[] range : sorted) {
        int[] open = merged.isEmpty() ? null : merged.get(merged.size() - 1);
        if (open != null && range[0] <= open[1]) {
            open[1] = Math.max(open[1], range[1]); // joins the open range
        } else {
            merged.add(new int[] {range[0], range[1]}); // a copy, so the input is never changed
        }
    }
    return merged.toArray(new int[0][]);
}
```

The `Math.max` matters: a long range can swallow later ranges whole, and then the open range must not shrink.

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Sort by start, then one sweep | O(n log n) | O(n) |
| Compare every pair and merge, repeat until stable | O(n squared) per pass, with several passes when ranges chain | O(n) |

## Where it shows up

`hold-exposure` reasons about the same endpoints: it sorts the starts and ends of holds and walks them in order. The difference worth saying aloud is the treatment of touching ends. There an end is exclusive, so a hold ending at minute 10 and another starting at minute 10 do not overlap and never count together. Here the ranges are closed, so ranges that touch are one group. Which rule applies is the first thing to settle with the interviewer in any intervals question.

## Follow-up answers

**What if the ranges arrive one at a time and you must keep the merged list up to date?** Keep the merged ranges in a sorted structure, a balanced tree keyed by start. For each new range, find the neighbours it touches, remove them, widen the new range over them and insert it. Each range is removed at most once, so the cost per arrival is O(log n) amortised.

**How would you list the gaps between the merged ranges instead?** Merge first. The gaps are then the stretches between the end of one merged range and the start of the next. There is no gap when ranges touch, because they were merged.

**What changes if ranges that only touch must stay separate?** Make the join test strict: a range joins the open one only when it starts before the open one ends. Everything else stays the same.
