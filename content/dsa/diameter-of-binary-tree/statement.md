# Diameter of Binary Tree

You are given the root of a binary tree. Return the length of its longest path, counted in edges: a path goes from one node to another along parent-child links without visiting any node twice. The path does not have to pass through the root.

Implement `Diameter.diameter(TreeNode root)` in `Diameter.java`. `TreeNode` (in `TreeNode.java`, read-only) has an `int val` and the children `left` and `right`. Trees below are written in level order: the values level by level, left to right, with `null` for a missing child and trailing `null`s left out. For example `[1,8,null,4]` is a root 1 whose left child 8 has a left child 4.

## Example 1

**Input:** `root = [2,7,5,1,6,null,9,null,null,3,8]`
**Output:** `5`
**Explanation:** The path 3, 6, 7, 2, 5, 9 climbs from 3 to the root and comes down to 9: five edges. No other path is longer.

## Example 2

**Input:** `root = [1,8,null,4,3,6,null,null,2]`
**Output:** `4`
**Explanation:** The longest path is 6, 4, 8, 3, 2, which turns at 8 and never touches the root; any path through the root is at most 3 edges long.

## Example 3

**Input:** `root = [11]`
**Output:** `0`
**Explanation:** A single node is a path with no edges.

## Constraints

- `1 <= number of nodes <= 10^5`
- `-10^4 <= node value <= 10^4`
- `1 <= depth <= 1000`, where depth is the number of nodes on the longest path from the root down to a leaf

## Follow-up

Can you do it in one pass over the tree, visiting each node once?
