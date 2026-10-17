# Binary Tree Maximum Path Sum

You are given the root of a binary tree whose nodes hold integers, some of them negative. A path is a sequence of nodes in which each pair of neighbours is joined by a parent-child link and no node appears twice. A path has at least one node and does not have to pass through the root. Its sum is the total of its values. Return the largest sum of any path in the tree.

Implement `MaxPathSum.maxPathSum(TreeNode root)` in `MaxPathSum.java`. `TreeNode` (in `TreeNode.java`, read-only) has an `int val` and the children `left` and `right`. Trees below are written in level order: the values level by level, left to right, with `null` for a missing child and trailing `null`s left out. For example `[6,-9,2]` is a root 6 with the left child -9 and the right child 2.

## Example 1

**Input:** `root = [-4,9,6,-2,7,null,-5,null,null,null,null,8]`
**Output:** `21`
**Explanation:** The path 7, 9, -4, 6, -5, 8 climbs from 7 to the root and comes down the other side to 8: 7 + 9 - 4 + 6 - 5 + 8 = 21. It is worth crossing -4 and -5 to reach the values behind them; it is not worth adding -2.

## Example 2

**Input:** `root = [-3,12,-8,5,6]`
**Output:** `23`
**Explanation:** The path 5, 12, 6 turns at 12 and never reaches the root. Any path through the root adds -3 and can use only one of 5 and 6.

## Example 3

**Input:** `root = [-7,-3,-9]`
**Output:** `-3`
**Explanation:** Every value is negative, so the best path is the single largest node. A path cannot be empty, so the answer is not 0.

## Constraints

- `1 <= number of nodes <= 10^5`
- `-10^4 <= node value <= 10^4`
- `1 <= depth <= 1000`, where depth is the number of nodes on the longest path from the root down to a leaf

## Follow-up

Can you find the answer in one pass over the tree, visiting each node once?
