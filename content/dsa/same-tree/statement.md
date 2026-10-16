# Same Tree

You are given the roots of two binary trees, `p` and `q`. Return `true` when they are the same tree: the same shape, with equal values in matching positions. Otherwise return `false`. The two trees are separate objects; compare what they hold, not whether they are the same nodes.

Implement `SameTree.isSame(TreeNode p, TreeNode q)` in `SameTree.java`. `TreeNode` (in `TreeNode.java`, read-only) has an `int val` and the children `left` and `right`; an empty tree is `null`. Trees below are written in level order: the values level by level, left to right, with `null` for a missing child and trailing `null`s left out. For example `[7,2,9,null,4]` is a root 7 with children 2 and 9, where 2 has only a right child, 4.

## Example 1

**Input:** `p = [7,2,9,null,4]`, `q = [7,2,9,null,4]`
**Output:** `true`
**Explanation:** Both are 7 over 2 and 9, with 4 as the right child of 2.

## Example 2

**Input:** `p = [7,2,9,null,4]`, `q = [7,2,9,4]`
**Output:** `false`
**Explanation:** The values match, but 4 is the right child of 2 in `p` and its left child in `q`.

## Example 3

**Input:** `p = []`, `q = [0]`
**Output:** `false`
**Explanation:** An empty tree and a tree of one node have different shapes, whatever the value.

## Constraints

- `0 <= number of nodes in each tree <= 10^5`
- `-10^4 <= node value <= 10^4`
- `0 <= depth of each tree <= 1000`, where depth is the number of nodes on the longest path from the root down to a leaf

## Follow-up

Can you stop at the first difference instead of reading both trees whole?
