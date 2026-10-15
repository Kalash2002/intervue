# How a strong round goes: Find if Path Exists in Graph

## Approach

This is reachability: can I get from one node to another following edges. The edges are two-way, so each connector has to be recorded at both of its ends. If I scanned the list of connectors every time I wanted the neighbours of a station, I would be repeating work, so I first group the connectors by station: for each station, the list of stations it touches.

Then I run a breadth-first search from `from`. I keep a visited mark per station so that a loop in the network cannot send me round forever, and I return `true` as soon as I take `to` off the queue. If the queue empties, everything reachable has been seen and `to` was not among it. The case `from == to` is handled by the same search, since the start counts as reached.

```java
static boolean connected(int n, int[][] links, int from, int to) {
    List<List<Integer>> next = new ArrayList<>();
    for (int i = 0; i < n; i++) {
        next.add(new ArrayList<>());
    }
    for (int[] link : links) {
        next.get(link[0]).add(link[1]); // both directions
        next.get(link[1]).add(link[0]);
    }
    boolean[] seen = new boolean[n];
    Deque<Integer> queue = new ArrayDeque<>();
    seen[from] = true;
    queue.add(from);
    while (!queue.isEmpty()) {
        int station = queue.poll();
        if (station == to) {
            return true;
        }
        for (int neighbour : next.get(station)) {
            if (!seen[neighbour]) {
                seen[neighbour] = true; // mark when queued, so it is queued once
                queue.add(neighbour);
            }
        }
    }
    return false;
}
```

Union-find does the same with less memory: join the two ends of each connector, then compare the roots of `from` and `to`. It is the better choice when the same network is asked many questions.

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Adjacency lists and breadth-first search | O(n + m) | O(n + m) |
| Union-find over the connectors | O(m times a small inverse-Ackermann factor) | O(n) |
| Repeatedly rescan all connectors until nothing new is reached | O(n times m) in the worst case | O(n) |

## Where it shows up

`friend-suggestion` stores friendships as a graph and walks it to find people a few steps away. The first thing that code needs is the same as here: record each friendship at both people, because a friendship is two-way, and keep a visited set so a cycle of friends does not loop. The search over those lists is this question; `degreesApart` only adds the distance.

## Follow-up answers

**How would you answer many from-to questions on the same network?** Build the structure once. With union-find, each question is two finds. Or label every station with its component number in one pass of searches, and then two stations are connected exactly when their labels match.

**What changes if the connectors work in one direction only?** Add each connector at one end only. The search is otherwise the same, but the answer is no longer symmetric: `from` reaching `to` does not mean `to` reaches `from`, and union-find no longer applies.

**How would you return an actual route, not just yes or no?** Record for each station the station it was reached from when it is first queued. When `to` is reached, follow those parents back to `from` and reverse the list. Breadth-first search makes that route the one with the fewest connectors.
