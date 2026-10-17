# Count Good Nodes in Binary Tree

You are given the root of a binary tree. Call a node good when no node on the path from the root down to it holds a larger value: its value is at least every value above it. A tie still counts as good, and the root, with nothing above it, is always good. Return the number of good nodes.

Implement `GoodNodes.goodNodes(TreeNode root)` in `GoodNodes.java`. `TreeNode` (in `TreeNode.java`, read-only) has an `int val` and the children `left` and `right`. Trees below are written in level order: the values level by level, left to right, with `null` for a missing child and trailing `null`s left out. For example `[2,null,6]` is a root 2 whose right child is 6.

## Example 1

**Input:** `root = [5,3,8,6,1,7,9,null,6]`
**Output:** `5`
**Explanation:** 5, 8 and 9 are good, and so is the 6 under 3, because the largest value above it is 5; the 6 below that one ties the largest value above it, 6, and is good too. 3, 1 and 7 each have a larger value above them.

## Example 2

**Input:** `root = [-4,-7,-2,null,-5]`
**Output:** `2`
**Explanation:** The root -4 and its right child -2 are good; -7 and the -5 below it are both smaller than -4.

## Example 3

**Input:** `root = [-10000]`
**Output:** `1`
**Explanation:** A single node is the root, and the root is always good, however small its value.

## Constraints

- `1 <= number of nodes <= 10^5`
- `-10^4 <= node value <= 10^4`
- `1 <= depth <= 1000`, where depth is the number of nodes on the longest path from the root down to a leaf

## Follow-up

Can you count them in one pass over the tree, without looking back up the path at each node?
