# Lowest Common Ancestor of a BST

You are given the root of a binary search tree and two different nodes of it, `p` and `q`. In a binary search tree every value in a node's left subtree is smaller than the node's value and every value in its right subtree is larger. Return the lowest common ancestor of `p` and `q`: the deepest node that has both of them in its subtree. A node counts as part of its own subtree, so when one of the two lies below the other, the upper one is the answer.

Implement `CommonAncestor.lca(TreeNode root, TreeNode p, TreeNode q)` in `CommonAncestor.java` and return the node itself, not a new node with the same value. `TreeNode` (in `TreeNode.java`, read-only) has an `int val` and the children `left` and `right`. Trees below are written in level order: the values level by level, left to right, with `null` for a missing child and trailing `null`s left out. For example `[8,3,null,5]` is a root 8 whose left child 3 has a left child 5. `p` and `q` are written as the values they hold, and so is the answer. The tests ask for many pairs in the same large tree.

## Example 1

**Input:** `root = [40,20,60,10,30,50,70,null,15,25]`, `p = 15`, `q = 25`
**Output:** `20`
**Explanation:** 15 hangs below 10 on the left of 20, and 25 hangs below 30 on the right of 20, so 20 is the lowest node above both.

## Example 2

**Input:** `root = [40,20,60,10,30,50,70,null,15,25]`, `p = 30`, `q = 25`
**Output:** `30`
**Explanation:** 25 is the left child of 30, and 30 is in its own subtree, so 30 is the answer.

## Example 3

**Input:** `root = [-6,null,9]`, `p = 9`, `q = -6`
**Output:** `-6`
**Explanation:** With two nodes, the root is above the other one; the order in which `p` and `q` are given does not matter.

## Constraints

- `2 <= number of nodes <= 10^5`
- `-10^9 <= node value <= 10^9`
- all values are distinct and the tree is a valid binary search tree
- `p != q`, and both are nodes of the tree
- `2 <= depth <= 1000`, where depth is the number of nodes on the longest path from the root down to a leaf

## Follow-up

Can you answer each pair in time proportional to the depth of the tree, using constant extra space?
