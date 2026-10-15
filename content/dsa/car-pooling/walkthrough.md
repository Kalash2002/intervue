# How a strong round goes: Car Pooling

## Approach

Which group sits where does not matter. The shuttle is fine exactly when the number of people aboard never goes above the seats, and that number changes only at a boarding minute or a leaving minute. So I do not look at every minute, and I do not compare every pair of requests.

I turn each request into two events: plus `riders` at `boardAt`, and minus `riders` at `leaveAt`. I sort the events by minute. At the same minute the removals must come first, because people who leave step off before the next group steps on; sorting by the signed amount at equal minutes does that, since removals are negative. Then I walk through the events keeping a running total and return `false` the moment it goes above `seats`. If I get to the end, the answer is `true`.

```java
static boolean canCarry(int[][] rides, int seats) {
    int[][] events = new int[rides.length * 2][];
    for (int i = 0; i < rides.length; i++) {
        events[2 * i] = new int[] {rides[i][1], rides[i][0]};      // boards: +riders
        events[2 * i + 1] = new int[] {rides[i][2], -rides[i][0]}; // leaves: -riders
    }
    Arrays.sort(events, (a, b) -> a[0] != b[0]
            ? Integer.compare(a[0], b[0])
            : Integer.compare(a[1], b[1])); // at one minute, removals first
    int aboard = 0;
    for (int[] event : events) {
        aboard += event[1];
        if (aboard > seats) {
            return false;
        }
    }
    return true;
}
```

When the minutes are small whole numbers, a difference array indexed by minute does the same job without a sort: add at the start, subtract at the end, and take running sums.

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Sort the events, one pass with a running total | O(n log n) | O(n) |
| Difference array over minutes (small time range only) | O(n + T) for T minutes | O(T) |
| For each request, add up the requests aboard at its boarding minute | O(n squared) | O(1) |

## Where it shows up

`hold-exposure` has the same shape: each hold is an interval that adds an amount while it is active, and `firstBreach` is the first moment the running total goes above a limit. Here the amount is a head count and the limit is the seats, and in both the end is processed before a start at the same time.

## Follow-up answers

**How would you report the first minute at which the shuttle is over capacity?** Return the minute of the event at which the running total first exceeds `seats` instead of `false`. Because removals sort first at a shared minute, that minute is a real boarding minute and not an artefact of ordering.

**What changes if the shuttle can also refuse some requests and you want to serve as many riders as possible?** The yes or no check becomes an optimisation. It is a weighted interval selection under a capacity, and a simple greedy no longer works in general; with one seat it is the classic weighted interval scheduling, solved by dynamic programming over requests sorted by end time.

**What if the times are tiny, say minutes within one day, rather than up to a billion?** Use the difference array: 1,441 counters, add at the start, subtract at the end, then one running sum. It avoids the sort and is linear.
