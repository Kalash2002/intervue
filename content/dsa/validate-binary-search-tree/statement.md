# Validate Binary Search Tree

You are given the root of a binary tree. Decide whether it is a binary search tree under the strict rule: for every node, every value in its left subtree is smaller than the node's value, and every value in its right subtree is larger. The rule covers whole subtrees, not just a node's two children. Equal values are never allowed, on either side, so a tree that holds the same value twice is never valid.

Implement `ValidBst.isValidBST(TreeNode root)` in `ValidBst.java`. `TreeNode` (in `TreeNode.java`, read-only) has an `int val` and the children `left` and `right`. Values may be anywhere in the `int` range, including `Integer.MIN_VALUE` and `Integer.MAX_VALUE`. Trees below are written in level order: the values level by level, left to right, with `null` for a missing child and trailing `null`s left out. For example `[2,null,6]` is a root 2 whose right child is 6.

## Example 1

**Input:** `root = [41,17,63,8,29,50,77]`
**Output:** `true`
**Explanation:** Everything left of 41 (17, 8, 29) is smaller and everything right of it (63, 50, 77) is larger, and the same holds at 17 and at 63.

## Example 2

**Input:** `root = [41,17,63,8,29,35,77]`
**Output:** `false`
**Explanation:** 35 is a fine left child for 63, but it sits in the right subtree of 41 and is smaller than 41.

## Example 3

**Input:** `root = [-2147483648,null,2147483647]`
**Output:** `true`
**Explanation:** The smallest and largest `int` values are legal values: the right child is larger than the root, so the tree is valid.

## Constraints

- `1 <= number of nodes <= 10^5`
- `-2^31 <= node value <= 2^31 - 1`
- `1 <= depth <= 1000`, where depth is the number of nodes on the longest path from the root down to a leaf

## Follow-up

Can you check it in one pass, reading each node once?
