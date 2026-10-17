# Binary Tree Level Order Traversal

You are given the root of a binary tree. Return its values level by level: first the root, then the values one step below it, then two steps below, and so on. Inside a level, list the values from left to right. An empty tree has no levels, so the answer is an empty list.

Implement `LevelOrder.levels(TreeNode root)` in `LevelOrder.java`. `TreeNode` (in `TreeNode.java`, read-only) has an `int val` and the children `left` and `right`; an empty tree is `null`. Trees below are written in level order: the values level by level, left to right, with `null` for a missing child and trailing `null`s left out. For example `[2,null,6]` is a root 2 whose right child is 6.

## Example 1

**Input:** `root = [7,3,11,null,5,9,2,8]`
**Output:** `[[7],[3,11],[5,9,2],[8]]`
**Explanation:** 3 has only a right child, 5, so the third level reads 5 from under 3, then 9 and 2 from under 11; the 8 below 5 is alone on the fourth level.

## Example 2

**Input:** `root = [4,-1,null,null,6,0]`
**Output:** `[[4],[-1],[6],[0]]`
**Explanation:** The tree is a single path that turns left, then right, then left, so each level holds one value.

## Example 3

**Input:** `root = []`
**Output:** `[]`
**Explanation:** An empty tree has no levels.

## Constraints

- `0 <= number of nodes <= 10^5`
- `-10^4 <= node value <= 10^4`
- `0 <= depth <= 1000`, where depth is the number of nodes on the longest path from the root down to a leaf

## Follow-up

Can you produce the levels in one pass over the tree, keeping no more than one level or one root-to-leaf path in memory besides the answer?
