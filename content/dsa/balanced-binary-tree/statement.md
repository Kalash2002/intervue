# Balanced Binary Tree

You are given the root of a binary tree. Return `true` when the tree is height-balanced: at every node, the height of the left subtree and the height of the right subtree differ by at most 1. Heights count nodes on the longest downward path, so an empty subtree has height 0 and a single node height 1. Otherwise return `false`.

Implement `Balanced.isBalanced(TreeNode root)` in `Balanced.java`. `TreeNode` (in `TreeNode.java`, read-only) has an `int val` and the children `left` and `right`; an empty tree is `null`. Trees below are written in level order: the values level by level, left to right, with `null` for a missing child and trailing `null`s left out. For example `[2,1,null,0]` is a root 2 whose left child 1 has a left child 0.

## Example 1

**Input:** `root = [8,4,12,2,6,null,14,1]`
**Output:** `true`
**Explanation:** At 8 the sides are 3 and 2 tall, at 4 they are 2 and 1, at 12 they are 0 and 1, at 2 they are 1 and 0; every leaf has two empty sides. No gap is larger than 1.

## Example 2

**Input:** `root = [5,3,9,1,null,7,10,0,null,6]`
**Output:** `false`
**Explanation:** Both sides of the root are 3 tall, but at 3 the left side (1 then 0) is 2 tall and the right side is empty, a gap of 2.

## Example 3

**Input:** `root = []`
**Output:** `true`
**Explanation:** An empty tree has no node where the sides could differ.

## Constraints

- `0 <= number of nodes <= 10^5`
- `-10^4 <= node value <= 10^4`
- `0 <= depth <= 1000`, where depth is the number of nodes on the longest path from the root down to a leaf

## Follow-up

Can you decide it in one pass, computing each height only once?
