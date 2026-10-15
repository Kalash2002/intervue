# How a strong round goes: Car Fleet

## Approach

Every car has a time it would need alone: the distance left divided by its speed. A car that is not blocked arrives at that time. A car behind catches the group ahead if, alone, it would arrive no later than that group, because then it reaches the group's tail before the gate and must slow down to join it. If it would arrive later, it never catches up and forms a group of its own. So I order the cars by start position and walk from the one nearest the gate to the one furthest back, keeping the arrival time of the group in front, the slowest time seen so far. A car with a strictly larger time starts a new group and becomes the new front; any other car, including one with exactly the same time, is absorbed. I use `>` for that comparison, so equal times join, as the statement says. The sort is what makes the single pass valid: without it the cars are not in road order, and processing them in input order would count wrongly. To sort by start position I pack each car into one `long` (position in the high bits, speed in the low 20 bits, since a speed is below 2 to the power 20) and sort the `long` array, which is fast and needs no objects. Dividing two integers in a `double` is exact enough here: equal ratios give equal doubles, and different ratios of numbers this size differ by far more than the rounding error.

```java
static int groups(int gate, int[] start, int[] speed) {
    int n = start.length;
    long[] cars = new long[n];
    for (int i = 0; i < n; i++) {
        cars[i] = ((long) start[i] << 20) | speed[i]; // sorts by start position
    }
    Arrays.sort(cars);
    int groups = 0;
    double slowest = 0; // arrival time of the group at the front
    for (int i = n - 1; i >= 0; i--) { // from the car nearest the gate backwards
        int position = (int) (cars[i] >> 20);
        int carSpeed = (int) (cars[i] & 0xFFFFF);
        double time = (double) (gate - position) / carSpeed;
        if (time > slowest) { // arrives later than the group ahead: a new group
            groups++;
            slowest = time;
        }
    }
    return groups;
}
```

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| For each car, check every car ahead for one that arrives no earlier | O(n squared) | O(1) |
| Sort by position, one pass keeping the front group's time | O(n log n) | O(n) |

## Where it shows up

Grouping things that catch up with each other and then move at the slowest pace shows up in traffic and pipeline models: requests queued behind a slow handler, or batches of jobs on a single worker line that cannot overtake. It is also the idea behind bunching in public transport timetables.

## Follow-up answers

**What changes if cars could overtake one another on a wide road?** Then no car is ever slowed, every car arrives at its own time, and the number of groups is simply the number of distinct arrival times if you count equal times as one group.

**How would you also report the time at which each group arrives?** Record `time` whenever a new group starts. That value is the arrival time of the group, since every car that joins it arrives together with its front car.

**Why do you not need a real stack, and when would you want one?** Only the time of the group at the front matters for the next car, so one number replaces the stack. A stack of times is the same idea written out and is useful if you also need each group, not just the count.
