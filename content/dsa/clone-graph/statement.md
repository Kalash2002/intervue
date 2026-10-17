# Clone Graph

A connected undirected graph is made of `GraphNode` objects. Each node holds an integer `val` and a list `neighbors` of the nodes it is connected to; a connection between two nodes appears in both of their lists. You are given one node of the graph.

Implement `CloneGraph.copy(GraphNode start)` in `CloneGraph.java`. Build a new graph with the same shape and return the new node that corresponds to `start`. Every node of the copy must be a new object, no list in the copy may point at a node of the input, and each copied node lists its neighbours in the same order as its original. Leave the input graph as it was. If `start` is `null`, return `null`.

In the examples a graph is written as `values` and `adjacency`: node `i` holds `values[i]` and its neighbour list is `adjacency[i]`, and `start` is node `0`. `GraphNode.of(values, adjacency)` builds such a graph.

## Example 1

**Input:** `values = [6, 2, 6, 9, 2]`, `adjacency = [[2, 1], [0, 2], [3, 1, 0], [4, 2], [3]]`
**Output:** `values = [6, 2, 6, 9, 2]`, `adjacency = [[2, 1], [0, 2], [3, 1, 0], [4, 2], [3]]`, made of five new nodes
**Explanation:** Nodes 0, 1 and 2 form a triangle, and a tail runs from node 2 through node 3 to node 4. Two nodes hold 6 and two hold 2, so a copy has to tell nodes apart by which object they are, not by their value.

## Example 2

**Input:** `values = [7]`, `adjacency = [[]]`
**Output:** `values = [7]`, `adjacency = [[]]`, made of one new node
**Explanation:** A graph of one node with no connections copies to one new node with an empty list.

## Example 3

**Input:** `start = null`
**Output:** `null`
**Explanation:** There is no graph, so there is nothing to copy.

## Constraints

- `0 <= number of nodes <= 10^5`
- `0 <= number of connections <= 2 * 10^5`
- `-1000 <= val <= 1000`
- Values may repeat.
- The graph is connected, has no connection from a node to itself, and no two connections join the same pair of nodes.

## Follow-up

What changes if the graph may be disconnected?
