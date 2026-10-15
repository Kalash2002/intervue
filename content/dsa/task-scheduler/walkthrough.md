# How a strong round goes: Task Scheduler

## Approach

The busiest kind sets the shape of the whole schedule, because its jobs are the hardest to spread out. If a kind has `most` jobs, they split the schedule into `most - 1` frames, each `gap + 1` slots long (the job itself plus the `gap` slots that must follow), and then a final slot for the last of them. Every other job can be dropped into the slots of those frames, and any slot nobody fills stays idle. Kinds that tie for the highest count each need a slot in the last stretch, so the lower bound is `(most - 1) * (gap + 1) + tied`, where `tied` is how many kinds have that count.

That bound can be too small. When there are so many other jobs that the frames overflow, the extra jobs simply extend the schedule and no slot is idle, so the answer is the number of jobs. The result is the larger of the two numbers.

I could also simulate this with a max-heap of remaining counts and a queue of kinds that are cooling down, always running the busiest kind that is allowed. It gives the same answer and it is what you need if the question asks for the actual order, but for the length alone the count formula is a single pass.

```java
static int minSlots(char[] jobs, int gap) {
    int[] count = new int[26];
    int most = 0;
    for (char job : jobs) {
        most = Math.max(most, ++count[job - 'A']);
    }
    int tied = 0; // how many kinds have the highest count
    for (int c : count) {
        if (c == most) {
            tied++;
        }
    }
    if (most == 0) {
        return 0;
    }
    long frame = (long) (most - 1) * (gap + 1) + tied;
    return (int) Math.max(frame, jobs.length); // with enough other jobs there is no idle slot
}
```

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Count the kinds and use the frame formula | O(n) | O(1), 26 counters |
| Max-heap of counts plus a cool-down queue | O(answer x log 26) | O(1) |
| Simulate slot by slot, scan all 26 kinds each time | O(answer x 26), and the answer can be about n x gap | O(1) |

## Where it shows up

`courier-load-balancer` makes the same greedy choice with a heap: each parcel goes to the courier with the least load, found with a min-heap keyed by load and index. Here the key is the remaining count and the thing taken from the heap is the busiest kind, but the shape is identical: repeatedly take the best candidate, change it, and put it back where its new key belongs.

## Follow-up answers

**How would you output an actual schedule, not just its length?** Use the heap version. Keep a max-heap of remaining counts and a queue of kinds that have just run. Each slot, release from the queue any kind whose gap is over, take the busiest from the heap, run it, and queue it with its new count. If the heap is empty, the slot is idle.

**What if each job kind had its own cool-down?** The closed formula no longer holds, because the frames differ in length. Fall back to the simulation: a heap of ready kinds and a time-ordered queue of cooling kinds, each with its own release time.

**What changes if the jobs must run in the order given?** Then there is nothing to choose. Walk the list, remember the last slot used by each kind, and when the next job is too soon, wait. The total is a single pass.
