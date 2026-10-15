# How a strong round goes: Design Hit Counter

## Approach

Because calls come in time order, a ping that is too old now is too old for every later call too. So I can throw it away for good instead of re-checking it each time. The pings are stored in the order they arrive, which is also time order, so the oldest are always at the front.

I keep the times in a queue. Before answering `recent`, and also when recording a ping, I remove from the front every time that is at or before `second - window`; those are no longer inside the window. What is left is exactly the recent pings, so `recent` returns the size. Each ping is added once and removed at most once, so the total work is linear in the number of calls, even though a single call may remove many.

```java
class Pings {
    private final int window;
    private final Deque<Integer> times = new ArrayDeque<>();

    Pings(int window) {
        this.window = window;
    }

    void ping(int second) {
        drop(second);
        times.addLast(second);
    }

    int recent(int second) {
        drop(second);
        return times.size();
    }

    private void drop(int second) {
        // the window is (second - window, second], so a time equal to second - window is out
        while (!times.isEmpty() && times.peekFirst() <= second - window) {
            times.removeFirst();
        }
    }
}
```

The `second - window` can go negative, which is fine for an `int` because both are at most a billion.

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Queue of times, drop from the front | O(1) amortised per call | O(pings inside the window) |
| Keep every ping and scan the whole list on each `recent` | O(n) per `recent` | O(all pings) |

## Where it shows up

`rate-limiter-audit` checks a log of requests against a rule such as "no more than N requests from one client in any T seconds". The check is this counter: for each request, drop the times that fell out of the window and compare the size with the limit. The window boundary is the part to get exactly right there too, whether a request exactly T seconds old still counts.

## Follow-up answers

**What if a million pings arrive in the same second?** Store a count per second instead of one entry per ping: a queue of pairs of second and count, plus a running total. Adding a ping to the latest second is one increment, and dropping the oldest seconds subtracts their counts from the total. Space is bounded by the number of distinct seconds in the window.

**What if the counter must be safe to call from many threads?** The simple answer is to make the two methods synchronized, since both touch the queue. If that becomes a bottleneck, use a ring of per-second counters with atomic increments, which gives an approximate edge at the window boundary, and say so.

**How would you answer for an arbitrary earlier time, not only the latest?** Keep all the times in a sorted array or list and answer with two binary searches: the position of the first time greater than `second - window` and of the first greater than `second`, and subtract. That costs O(log n) per query but keeps all the history.
