# How a strong round goes: Meeting Rooms III

## Approach

The rules are a simulation, so I handle the requests in order of start and keep the state of the rooms as I go. Two choices come up at each request. When some rooms are free, I want the lowest-numbered one. When none is free, I want the room that becomes free first, with the lower number winning a tie. Those are two different orderings over the rooms, and scanning every room for each request is too slow when there are many rooms. A min-heap gives the smallest of each ordering in logarithmic time.

So I keep one heap of free room numbers and one heap of busy rooms ordered by the minute they finish, then by number. For a request starting at `start`, I first move every busy room that has finished at or before `start` into the free heap. If the free heap has a room, I take its smallest number and the session runs as asked. Otherwise I take the busy room that finishes first; the session starts when that room finishes, so its new finish is that time plus the request's length. In both cases I add one to the room's session count and put the room into the busy heap. The finishing times of delayed sessions can pass the `int` range, so they are `long`. At the end I return the first room with the largest count.

```java
static int busiest(int rooms, int[][] bookings) {
    int[][] byStart = bookings.clone();
    Arrays.sort(byStart, (a, b) -> Integer.compare(a[0], b[0]));
    PriorityQueue<Integer> free = new PriorityQueue<>();
    for (int room = 0; room < rooms; room++) {
        free.add(room);
    }
    // busy rooms: {finishing minute, room}, earliest finish first, lower room on ties
    PriorityQueue<long[]> busy = new PriorityQueue<>(
            (a, b) -> a[0] != b[0] ? Long.compare(a[0], b[0]) : Long.compare(a[1], b[1]));
    int[] hosted = new int[rooms];
    for (int[] booking : byStart) {
        while (!busy.isEmpty() && busy.peek()[0] <= booking[0]) {
            free.add((int) busy.poll()[1]); // finished by this start
        }
        int room;
        long finish;
        if (!free.isEmpty()) {
            room = free.poll();
            finish = booking[1];
        } else {
            long[] first = busy.poll(); // wait for the room that frees first
            room = (int) first[1];
            finish = first[0] + (booking[1] - booking[0]);
        }
        hosted[room]++;
        busy.add(new long[] {finish, room});
    }
    int best = 0;
    for (int room = 1; room < rooms; room++) {
        if (hosted[room] > hosted[best]) {
            best = room; // strictly greater, so ties keep the lower number
        }
    }
    return best;
}
```

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Sort by start, then a heap of free rooms and a heap of busy rooms | O((n + r) log r) for n requests and r rooms | O(r) |
| Sort by start, then scan all rooms for every request | O(n times r) | O(r) |

## Where it shows up

`courier-load-balancer` gives each parcel to the least-loaded courier, with the lowest index winning a tie. That is this question in a different costume: a heap keyed by a value and then an index, the item taken out, changed and put back, and a tie-break that has to live in the comparator because a heap keeps only the order it is given. The free and busy split here is the same idea applied twice.

## Follow-up answers

**What if a delayed booking should be dropped instead of waiting?** Then there is no busy-heap branch for the wait: if the free heap is empty after moving finished rooms, skip the request and do not count it. The remaining logic is unchanged.

**How would you report the total waiting time as well?** In the delayed branch, the wait is `first[0] - booking[0]`; add it to a running total. It is a `long`, since many requests can each wait a long time.

**What changes if bookings arrive live, one at a time, with no sorted list up front?** The heaps already work online as long as requests arrive in order of start, because nothing in the loop looks ahead. If they can arrive out of order, an earlier-start request could need a room that a later-start one already took, and you would have to buffer or reorder them first.
