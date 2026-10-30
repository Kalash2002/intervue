# Grader notes: Network Delay Time

Tests cover: the sample, an unreachable node, a single node, links that only work one way, a source that is not node 0, parallel links, a zero delay on a cycle, a long cheap detour that beats a direct expensive link, a far node that is the last to be reached through a direct link, and a large chain of 60,000 nodes whose links are listed from the far end back to the start.

The naive solution is Bellman-Ford: sweep over all links, relaxing each, until a sweep changes nothing. With the links listed in reverse along a chain, each sweep settles only one more node, so it needs about 60,000 sweeps of 60,000 links; it exceeds the limit and 3x the limit. The reference settles nodes in order of time with a priority queue.
