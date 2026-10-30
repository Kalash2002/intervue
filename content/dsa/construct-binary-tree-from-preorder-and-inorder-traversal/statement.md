# Construct Binary Tree from Preorder and Inorder Traversal

You are given two arrays that describe the same binary tree. `preorder` lists its values in pre-order (a node, then its left subtree, then its right subtree) and `inorder` lists them in in-order (the left subtree, then the node, then the right subtree). Rebuild the tree and return its root.

Implement `BuildTree.buildTree(int[] preorder, int[] inorder)` in `BuildTree.java`. `TreeNode` (in `TreeNode.java`, read-only) has an `int val` and the children `left` and `right`. Trees below are written in level order: the values level by level, left to right, with `null` for a missing child and trailing `null`s left out. For example `[5,null,9,2]` is a root 5 whose right child 9 has a left child 2.

## Example 1

**Input:** `preorder = [14,-3,8,40,19,27,6]`, `inorder = [8,-3,19,40,14,27,6]`
**Output:** `[14,-3,27,8,40,null,6,null,null,19]`
**Explanation:** Pre-order starts with the root, 14. In in-order, the four values before 14 form the left subtree and the two after it form the right subtree; the same split repeats inside each part.

## Example 2

**Input:** `preorder = [5,9,2]`, `inorder = [5,2,9]`
**Output:** `[5,null,9,2]`
**Explanation:** 5 comes first in in-order, so it has no left subtree. Of the rest, 9 is the root (it comes first in pre-order) and 2, before it in in-order, is its left child.

## Example 3

**Input:** `preorder = [4,3,2,1]`, `inorder = [1,2,3,4]`
**Output:** `[4,3,null,2,null,1]`
**Explanation:** Each root is the last of its in-order run, so every node has only a left child: the tree is a path leaning left, and pre-order is in-order reversed.

## Constraints

- `1 <= preorder.length <= 10^5`
- `inorder.length == preorder.length`
- `-10^9 <= preorder[i], inorder[i] <= 10^9`
- all values are distinct
- `preorder` and `inorder` are the pre-order and in-order sequences of one binary tree
- `1 <= depth <= 1000`, where depth is the number of nodes on the longest path from the root down to a leaf

## Follow-up

Can you rebuild the tree in time linear in the number of nodes?
