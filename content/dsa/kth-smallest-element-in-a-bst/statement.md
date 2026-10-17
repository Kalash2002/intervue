# Kth Smallest Element in a BST

You are given the root of a binary search tree and a number `k`. In a binary search tree every value in a node's left subtree is smaller than the node's value and every value in its right subtree is larger, and all values are distinct. Return the `k`-th smallest value in the tree, counting from 1: `k = 1` asks for the smallest value.

Implement `KthSmallest.kthSmallest(TreeNode root, int k)` in `KthSmallest.java`. `TreeNode` (in `TreeNode.java`, read-only) has an `int val` and the children `left` and `right`. Trees below are written in level order: the values level by level, left to right, with `null` for a missing child and trailing `null`s left out. For example `[8,3,null,5]` is a root 8 whose left child 3 has a left child 5. Each call should cost time that depends on the depth of the tree and on `k`, not on the size of the tree: the tests make about 2,000 calls with `k` up to 1,000 on a tree of 100,000 nodes, so reading the whole tree on every call is too slow.

## Example 1

**Input:** `root = [31,12,47,5,20,null,60,null,9]`, `k = 3`
**Output:** `12`
**Explanation:** In increasing order the values are 5, 9, 12, 20, 31, 47, 60, and the third of them is 12.

## Example 2

**Input:** `root = [31,12,47,5,20,null,60,null,9]`, `k = 7`
**Output:** `60`
**Explanation:** The tree holds seven values, so the seventh smallest is the largest one.

## Example 3

**Input:** `root = [-1000000000]`, `k = 1`
**Output:** `-1000000000`
**Explanation:** A single node holds the only value, and it is the smallest.

## Constraints

- `1 <= k <= number of nodes <= 10^5`
- `-10^9 <= node value <= 10^9`
- all values are distinct and the tree is a valid binary search tree
- `1 <= depth <= 1000`, where depth is the number of nodes on the longest path from the root down to a leaf

## Follow-up

If the tree changed often between calls and `k` could be anything up to its size, how would you answer each call quickly?
