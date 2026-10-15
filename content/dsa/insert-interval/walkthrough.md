# How a strong round goes: Insert Interval

## Approach

The list is sorted and its ranges do not overlap, so relative to the new range every existing range is in one of three groups, and the groups come in order: ranges that end before the new one starts, ranges that overlap or touch it, and ranges that start after it ends. I walk the list once. The first group is copied unchanged. For the middle group, I stretch the new range to the smallest start and largest end among itself and the ranges it meets, and drop those ranges. Then I add the stretched range and copy the third group. Touching counts as overlapping here, so "ends before" is a strict comparison and "reaches" is a less-or-equal one. The input list is never changed; I build a new one.

```java
static int[][] insert(int[][] booked, int[] added) {
    List<int[]> result = new ArrayList<>(booked.length + 1);
    int i = 0;
    while (i < booked.length && booked[i][1] < added[0]) { // ends before the new range starts
        result.add(booked[i++]);
    }
    int start = added[0];
    int end = added[1];
    while (i < booked.length && booked[i][0] <= end) { // reaches the new range: absorb it
        start = Math.min(start, booked[i][0]);
        end = Math.max(end, booked[i][1]);
        i++;
    }
    result.add(new int[] {start, end});
    while (i < booked.length) { // starts after the merged range ends
        result.add(booked[i++]);
    }
    return result.toArray(new int[0][]);
}
```

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Three-group walk with a cursor | O(n) | O(n) for the new list |
| Append, sort by start, then merge everything | O(n log n) | O(n) |
| Binary search for both ends, then copy | O(n) to copy, O(log n) to locate | O(n) |

Even the binary-search version has to build a new array, so O(n) is the floor for an array; a balanced tree makes the insert itself O(log n) amortised over the merged ranges.

## Where it shows up

`shift-roster-lookup` has a method that lists the stretches of a range that no nurse covers, and it does the same kind of walk: the shifts are sorted and do not overlap, so one cursor moves left to right, and the gaps appear between a shift that ends and the next one that starts. The comparisons that decide whether a range ends before, reaches or starts after the current position are the ones used here.

## Follow-up answers

**How would you do it if the list were stored in a balanced tree?** Find the first range that could reach the new one with a floor lookup on its start, then remove ranges one by one while they overlap, widening the new range, and insert the result. Each range is removed at most once, so the amortised cost is O(log n) per insert.

**What if the new range may arrive before the list is sorted?** Then the three-group walk is not valid. Sort first, or treat the input as unsorted and merge everything, which is the merge-intervals problem.

**How would you remove a range instead of adding one?** The same three groups apply. Ranges wholly inside the removed span disappear, and the two ends that stick out are trimmed. A range that contains the removed span splits into two pieces.
