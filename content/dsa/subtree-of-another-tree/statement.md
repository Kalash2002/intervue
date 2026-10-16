# Subtree of Another Tree

You are given the roots of two binary trees, `root` and `sub`. Return `true` when some node of `root`, together with everything below it, is identical to `sub`: the same shape with equal values in matching positions. A match must include the whole subtree; a node of `root` with extra descendants that `sub` lacks does not count. `root` itself counts as one of its subtrees.

Implement `Subtree.isSubtree(TreeNode root, TreeNode sub)` in `Subtree.java`. `TreeNode` (in `TreeNode.java`, read-only) has an `int val` and the children `left` and `right`. Trees below are written in level order: the values level by level, left to right, with `null` for a missing child and trailing `null`s left out. For example `[5,5,null,5]` is a root 5 whose left child 5 has a left child 5. Nothing limits how lopsided a tree may be: a single path through all the nodes is a valid input.

## Example 1

**Input:** `root = [9,4,6,1,5,null,8]`, `sub = [4,1,5]`
**Output:** `true`
**Explanation:** The node 4 of `root` has the children 1 and 5 and nothing below them, exactly like `sub`.

## Example 2

**Input:** `root = [9,4,6,1,5,null,8,null,null,3]`, `sub = [4,1,5]`
**Output:** `false`
**Explanation:** The node 4 still has the children 1 and 5, but 5 now has a child 3, so that subtree is not identical to `sub`, and no other node of `root` holds 4.

## Example 3

**Input:** `root = [5,5,null,5]`, `sub = [5,5]`
**Output:** `true`
**Explanation:** The root's subtree is three nodes deep, so it does not match, but its left child, a 5 over a single 5, does.

## Constraints

- `1 <= number of nodes in root <= 10^5`
- `1 <= number of nodes in sub <= 10^5`
- `-10^4 <= node value <= 10^4`

## Follow-up

Comparing `sub` at every node of `root` can take O(n·m) time. Can you do it in O(n + m)?
