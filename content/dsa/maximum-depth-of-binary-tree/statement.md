# Maximum Depth of Binary Tree

You are given the root of a binary tree. Return its depth: the number of nodes on the longest path that starts at the root and goes down to a leaf. A single node has depth 1, and an empty tree has depth 0.

Implement `MaxDepth.maxDepth(TreeNode root)` in `MaxDepth.java`. `TreeNode` (in `TreeNode.java`, read-only) has an `int val` and the children `left` and `right`; an empty tree is `null`. Trees below are written in level order: the values level by level, left to right, with `null` for a missing child and trailing `null`s left out. For example `[-3,null,8,null,2]` is a root -3 whose right child 8 has a right child 2.

## Example 1

**Input:** `root = [10,4,15,null,7,12,20,null,null,11]`
**Output:** `4`
**Explanation:** The path 10, 15, 12, 11 has four nodes; the paths through 4 and 20 stop after three.

## Example 2

**Input:** `root = [-3,null,8,null,2]`
**Output:** `3`
**Explanation:** Each node has only a right child, so the one path holds all three nodes.

## Example 3

**Input:** `root = []`
**Output:** `0`
**Explanation:** An empty tree has no nodes and no path, so its depth is 0.

## Constraints

- `0 <= number of nodes <= 10^5`
- `-10^4 <= node value <= 10^4`
- `0 <= depth <= 1000`, where depth is the number of nodes on the longest path from the root down to a leaf

## Follow-up

Can you do it without recursion, so that the depth limit no longer matters?
