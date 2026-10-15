# How a strong round goes: Longest Consecutive Sequence

## Approach

Sorting would put the runs side by side, and it works in O(n log n), but repeated values need care and the sort does more than the question asks. I want constant-time membership instead, so I put the values in a hash set. The clumsy way is to start from every value and walk upward while the next value is present. That repeats work: on a long run, every member walks to the end. The fix is to start only at the beginning of a run, a value whose predecessor `x - 1` is not in the set. From a start I walk up while the next value is present and track the longest length. A value in the middle of a run is skipped by the check, and every value is walked over at most once, from its run's start. So the total work is linear even though there is a loop in a loop. Because the set stores each value once, repeats cost nothing and count once.

```java
static int longest(int[] nums) {
    Set<Integer> values = new HashSet<>();
    for (int x : nums) {
        values.add(x); // repeats collapse into one entry
    }
    int best = 0;
    for (int x : values) {
        if (values.contains(x - 1)) {
            continue; // not the start of a run: its run is counted from the start
        }
        int length = 1;
        while (values.contains(x + length)) {
            length++; // walk up from the start
        }
        best = Math.max(best, length);
    }
    return best;
}
```

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Start from every value and walk up in the set | O(n squared) on one long run | O(n) |
| Sort, then scan and skip repeats | O(n log n) | O(1) to O(n), depending on the sort |
| Set, start only at the beginning of a run | O(n) | O(n) |

## Where it shows up

Looking for unbroken stretches in unordered data: the longest streak of consecutive days with activity in a log, the longest run of consecutive ids or sequence numbers received (finding gaps in a stream), and merging reservations or page ranges that touch.

## Follow-up answers

**Why does the set-based solution run in linear time even though it has a loop inside a loop?** The inner walk only starts at the beginning of a run, and a run's values are walked once. Summed over all runs, the inner loop takes at most one step per value in the set, so the total is O(n), not O(n squared).

**How does the answer change if you sort the array first, and what does that cost?** After sorting, runs are adjacent: scan once, add one to the length when the value is exactly one more than the previous, skip equal values, and reset on a gap. The scan is cheap but the sort makes the whole thing O(n log n).

**How would you also return the numbers of the longest run?** Keep the start value of the best run as well as its length, and return the values from the start up to start plus length minus one.
