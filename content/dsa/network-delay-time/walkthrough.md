# How a strong round goes: Network Delay Time

## Approach

The signal reaches each node at the earliest time any route can bring it, and the answer is the largest of those earliest times, or -1 if one is missing. So this is single-source shortest paths with non-negative delays, which is Dijkstra's algorithm. I keep the best known arrival time per node, starting with 0 at the source and infinity elsewhere, and a priority queue ordered by time. I take the entry with the smallest time. Since no delay is negative, nothing found later can beat it, so that node's time is final. Then I try each outgoing link: if the arrival time through it improves the best known time of the target, I record it and queue the target. A node can be queued more than once, so an entry that is older than the best known time is skipped when it comes out. At the end, if every node was settled, the time of the last one settled is the answer.

```java
static int timeToReachAll(int n, int[][] links, int source) {
    List<List<int[]>> out = new ArrayList<>();
    for (int i = 0; i < n; i++) {
        out.add(new ArrayList<>());
    }
    for (int[] link : links) {
        out.get(link[0]).add(new int[] {link[1], link[2]});
    }
    long[] best = new long[n];
    Arrays.fill(best, Long.MAX_VALUE);
    best[source] = 0;
    PriorityQueue<long[]> queue = new PriorityQueue<>((a, b) -> Long.compare(a[0], b[0]));
    queue.add(new long[] {0, source}); // time first, then node
    int settled = 0;
    long last = 0;
    while (!queue.isEmpty()) {
        long[] entry = queue.poll();
        int node = (int) entry[1];
        if (entry[0] > best[node]) {
            continue; // stale entry: a faster route was found after this was queued
        }
        settled++;
        last = entry[0];
        for (int[] link : out.get(node)) {
            long arrival = entry[0] + link[1];
            if (arrival < best[link[0]]) {
                best[link[0]] = arrival;
                queue.add(new long[] {arrival, link[0]});
            }
        }
    }
    return settled == n ? (int) last : -1;
}
```

Entries leave the queue in increasing time order, so the last settled node has the largest earliest time. Breadth-first search is not enough here: it counts links, and a route with fewer links can take longer.

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Dijkstra with a binary heap | O((V + E) log V) | O(V + E) |
| Dijkstra scanning an array for the smallest time | O(V squared + E) | O(V + E) |
| Bellman-Ford, sweeping all links until stable | O(V x E) | O(V) |

V is the number of nodes and E the number of links.

## Where it shows up

`corridor-router` is the same problem with one more rule. Without it, finding the cheapest route is exactly this: settle nodes in order of cost, relax their outgoing corridors. The router also limits the number of hops, and then the cheapest way to a node may not be the useful one, so settling each node once is no longer right and the walkthrough there switches to rounds. Solving this question first makes that limit visible: it is the one thing that breaks the argument that the smallest queued time is final.

## Follow-up answers

**What if some delays could be negative?** Dijkstra is then wrong, because a later, longer route can end up cheaper than a node already settled. Use Bellman-Ford, which handles negative links and also detects a negative cycle, where earlier arrival is possible forever and the time is not defined.

**How would you return the node that is reached last, not just the time?** Remember the node alongside the time each time you settle one. The final value of that pair is the answer. If several nodes tie, say which one you return.

**What if you needed the time for many different sources on the same network?** For a handful of sources, run the algorithm once per source. For many, and when the network is small, an all-pairs method such as Floyd-Warshall does it in O(V cubed), which pays off only on small graphs; on large ones you precompute landmarks or limit the questions asked.
