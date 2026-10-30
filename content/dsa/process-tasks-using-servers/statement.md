# Process Tasks Using Servers

A depot has several machines, numbered from 0, and each has a weight. Jobs numbered from 0 arrive one per second: job `j` arrives at second `j` and needs `durations[j]` seconds of work.

Jobs are handled in arrival order, and each is given to a machine by these rules:

- Only a free machine can take a job. A machine that took job `j` at second `s` is busy until second `s + durations[j]`, and is free again from that second on.
- Among the free machines, choose the one with the smallest weight; if weights are equal, the smaller number.
- If no machine is free, the job waits. As soon as a machine becomes free, the waiting job that arrived first takes it. When several machines become free at the same second, the waiting jobs take them one by one in arrival order, each applying the rule above.

Jobs can queue up behind one another on a busy machine, so the second at which a machine frees up can pass the largest `int`, 2,147,483,647. Choose your types with that in mind.

Implement `Dispatcher.assign(int[] weights, int[] durations)` in `Dispatcher.java`. It returns an array of the same length as `durations` whose entry `j` is the number of the machine that took job `j`.

## Example 1

**Input:** `weights = [4, 2, 7]`, `durations = [3, 3, 3, 1, 1]`
**Output:** `[1, 0, 2, 1, 1]`
**Explanation:** Jobs 0, 1 and 2 take the free machines in order of weight: 1, then 0, then 2. Machine 1 frees at second 3, so job 3 takes it. At second 4 machines 0 and 1 are both free and the lighter machine 1 takes job 4.

## Example 2

**Input:** `weights = [1, 1]`, `durations = [5, 5, 1, 1]`
**Output:** `[0, 1, 0, 0]`
**Explanation:** Jobs 2 and 3 find both machines busy. Machine 0 frees at second 5 and takes job 2 until second 6. At second 6 both are free, the weights tie, and job 3 goes to machine 0.

## Example 3

**Input:** `weights = [9]`, `durations = [2, 2, 2]`
**Output:** `[0, 0, 0]`
**Explanation:** One machine does everything in turn.

## Constraints

- `1 <= weights.length <= 2 * 10^5`
- `1 <= durations.length <= 2 * 10^5`
- `1 <= weights[i] <= 10^9`
- `1 <= durations[j] <= 10^9`

## Follow-up

How would you also report when each job started?
