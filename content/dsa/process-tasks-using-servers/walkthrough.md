# How a strong round goes: Process Tasks Using Servers

## Approach

Each job needs the best free machine, and when none is free it needs the first machine to finish. Those are two different orderings over two different groups, so I use two heaps. Free machines sit in a min-heap ordered by weight, then number. Busy machines sit in a min-heap ordered by the second they finish. A machine moves from the busy heap to the free heap when its second has come, and back when it takes a job.

I keep a clock `now`. For job `j` it becomes `max(now, j)`, because a job cannot start before it arrives and cannot start before the job ahead of it, which may itself have waited. Then I release every busy machine that finishes at or before `now`. If the free heap is still empty, every machine is busy, so I jump `now` to the earliest finishing second and release again, which also collects every machine finishing at that very second. Then I pop the best free machine, record it, and push it onto the busy heap with its new finishing second.

The finishing second is a `long`. With a single machine and long jobs the seconds add up to more than an int holds, and an int version quietly treats a machine as free too early.

```java
static int[] assign(int[] weights, int[] durations) {
    int machines = weights.length;
    PriorityQueue<Integer> free = new PriorityQueue<>(
            Comparator.<Integer>comparingInt(m -> weights[m]).thenComparingInt(m -> m));
    for (int m = 0; m < machines; m++) {
        free.add(m);
    }
    long[] freeAt = new long[machines];
    PriorityQueue<Integer> busy = new PriorityQueue<>(
            Comparator.<Integer>comparingLong(m -> freeAt[m]).thenComparingInt(m -> m));
    int[] answer = new int[durations.length];
    long now = 0;
    for (int job = 0; job < durations.length; job++) {
        now = Math.max(now, job);
        release(busy, free, freeAt, now);
        if (free.isEmpty()) {
            now = freeAt[busy.peek()]; // wait for the first machine to finish
            release(busy, free, freeAt, now);
        }
        int machine = free.poll();
        answer[job] = machine;
        freeAt[machine] = now + durations[job];
        busy.add(machine);
    }
    return answer;
}
```

`release` moves every busy machine with `freeAt <= now` into the free heap. Checking for `<=` matters: a machine that finishes at the second a job arrives is free for that job.

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Two heaps, free and busy | O((n + m) log m) | O(m) |
| Scan every machine for each job | O(n times m) | O(m) |

Here n is the number of jobs and m the number of machines.

## Where it shows up

`courier-load-balancer` keeps the same kind of heap in `LoadBalancer.plan`: a min-heap of `{load, courier index}`, so the lightest courier is on top and equal loads come out by index, which is the "listed first" tie-break. This question adds time. A machine is unavailable while busy, which is why a second heap holds it until its finishing second, whereas the courier is always available and only its load changes.

## Follow-up answers

**How would you also report when each job started?** Record `now` when the job takes its machine. It is the start second, and `now - j` is how long the job waited.

**What changes if jobs can arrive at arbitrary seconds instead of one per second?** Replace `j` by the arrival second of the job, and sort the jobs by arrival if they do not come in that order. The rest is unchanged: `now` becomes `max(now, arrival)`.

**What if a machine can break down while it is working?** The busy heap no longer knows the finishing second for certain. Remove the machine from it when it breaks, which needs a structure that deletes by key (a tree set instead of a heap), and decide whether its job restarts on another machine or fails.
