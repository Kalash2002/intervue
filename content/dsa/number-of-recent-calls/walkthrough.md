# How a strong round goes: Number of Recent Calls

## Approach

Time only moves forward, so a call that is too old for the current call is too old for every later one. That means I never need to look back at calls I have already dropped. I keep the arrival times in a queue, oldest at the front. On each ping I add the new time at the back, then remove from the front while the front is earlier than `t - window`. What is left are exactly the calls in the range, so the answer is the size of the queue.

Each time enters the queue once and leaves at most once, so the work over all pings is linear even though one ping can drop many calls. Scanning the whole history on every ping would also be correct, but it repeats work the queue has already done.

```java
private final int window;
private final Deque<Integer> recent = new ArrayDeque<>(); // arrival times, oldest first

int ping(int t) {
    recent.addLast(t);
    while (recent.peekFirst() < t - window) {
        recent.pollFirst(); // too old now, and too old for every later call
    }
    return recent.size();
}
```

The loop cannot run the queue empty, because the call just added is never older than `t - window`.

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Queue with expiry from the front | O(1) per ping on average, O(n) for n pings | O(calls inside the window) |
| Keep every time, count the recent ones by walking back each ping | O(calls inside the window) per ping | O(n) |

## Where it shows up

`rate-limiter-audit` runs this queue in `RateAudit.rejections`: a deque of one client's accepted request times in milliseconds, with the expired ones removed from the front before each decision. Two things differ. There only accepted requests enter the deque, and a request is rejected when the deque already holds `limit` of them. And its window is `(t - w, t]`, so a time exactly one window old has expired and is removed with `<=`, whereas here the range includes `t - window` and the loop removes only strictly older times.

## Follow-up answers

**What if calls can arrive out of order?** The queue idea breaks, because the front is no longer the oldest. Keep a sorted structure instead, such as a balanced tree of times, and count the ones in the range by two searches. Each ping then costs O(log n).

**How would you count calls in the window for several different clients at once?** Keep one queue per client in a map from client to queue. The cost per ping stays the same. Empty queues can be removed from the map so that idle clients do not take memory.

**What changes if the window is a count of the last k calls instead of a length of time?** Time stops mattering. Add the new call, and if the queue is longer than k remove one from the front. The size is then min(calls so far, k), so a counter would do and no queue is needed unless the times themselves are wanted.
