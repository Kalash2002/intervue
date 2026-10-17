# Binary Tree Right Side View

You are given the root of a binary tree. Picture standing to the right of it: on every level you see only the node furthest to the right. Return the values you see, from the top level down. A node on the left side of the tree is visible when nothing on its level lies to its right. An empty tree shows nothing, so the answer is an empty list.

Implement `RightView.rightSideView(TreeNode root)` in `RightView.java`. `TreeNode` (in `TreeNode.java`, read-only) has an `int val` and the children `left` and `right`; an empty tree is `null`. Trees below are written in level order: the values level by level, left to right, with `null` for a missing child and trailing `null`s left out. For example `[2,null,6]` is a root 2 whose right child is 6.

## Example 1

**Input:** `root = [12,6,15,4,8,null,20,null,null,7]`
**Output:** `[12,15,20,7]`
**Explanation:** The right edge shows 12, 15 and 20, but the 7 below 8 sits on a level where nothing lies to its right, so it shows too.

## Example 2

**Input:** `root = [3,1,null,null,2]`
**Output:** `[3,1,2]`
**Explanation:** The root has only a left child, 1, which is therefore the rightmost node of its level; 2 below it is alone on the last level.

## Example 3

**Input:** `root = []`
**Output:** `[]`
**Explanation:** An empty tree has no levels to see.

## Constraints

- `0 <= number of nodes <= 10^5`
- `-10^4 <= node value <= 10^4`
- `0 <= depth <= 1000`, where depth is the number of nodes on the longest path from the root down to a leaf

## Follow-up

Can you do it in one pass, keeping only one root-to-leaf path on the stack?
