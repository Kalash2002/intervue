# How a strong round goes: Meeting Rooms II

## Approach

I do not need to decide which booking goes in which room. The number of rooms I must own is the largest number of bookings that are active at the same moment: that many are needed together, and with that many a room is always free for the next one. That count only changes at a start or at an end, so I do not have to look at every minute.

I sort the start times and the end times separately. Then I walk through the starts in order. Before counting a booking as active, I release every booking whose end is at or before this start, because the end is exclusive and that room is free again. I then add one for the new booking and remember the highest count. The ends only have to be consumed in order, so one index into the sorted ends is enough.

```java
static int minRooms(int[][] bookings) {
    int n = bookings.length;
    int[] starts = new int[n];
    int[] ends = new int[n];
    for (int i = 0; i < n; i++) {
        starts[i] = bookings[i][0];
        ends[i] = bookings[i][1];
    }
    Arrays.sort(starts);
    Arrays.sort(ends);
    int busy = 0;
    int peak = 0;
    int released = 0; // how many bookings have already ended
    for (int start : starts) {
        while (ends[released] <= start) { // an end at this minute frees a room first
            released++;
            busy--;
        }
        busy++;
        peak = Math.max(peak, busy);
    }
    return peak;
}
```

A min-heap of end times gives the same answer: take the earliest end, reuse its room if it is at or before the start, and add the new end. It is one more structure to get right, and the two sorted arrays do the same job.

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Sort starts and ends, sweep once | O(n log n) | O(n) |
| Sort by start and keep a min-heap of end times | O(n log n) | O(n) |
| For each booking, count the bookings active at its start | O(n squared) | O(1) |

## Where it shows up

`hold-exposure` is this question with amounts. Each hold is an interval that adds money to an account while it is active, and the peak exposure is the largest running total over all moments, found by sorting the starts and ends of the holds together and keeping a running sum. Counting bookings is the same sweep with every amount equal to one. The rule about ends is shared too: an end at the same minute as a start is processed first.

## Follow-up answers

**How would you also say which room each booking gets?** Use the heap version. Keep a heap of rooms keyed by the time they become free. For each booking in order of start, take the room that frees earliest; if it is free by this start, give the booking that room, otherwise open a new one. Record the room number for the booking.

**What if every room is only available for part of the day?** The count no longer settles it, because a booking can only go to a room that is open for its whole duration. It becomes a matching problem; a greedy over bookings sorted by start with a structure of rooms sorted by closing time is the usual start, and you would check with the interviewer how general the room windows are.

**How would you find the busiest moment, not just the count?** Record the minute when `peak` is raised, which is the start of the booking that made the count higher. The busiest period then runs until the next end.
