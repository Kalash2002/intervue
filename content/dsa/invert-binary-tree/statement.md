# Invert Binary Tree

You are given the root of a binary tree. Turn it into its mirror image: at every node, the left child and the right child trade places, all the way down. Return the root.

Implement `InvertTree.invert(TreeNode root)` in `InvertTree.java`. `TreeNode` (in `TreeNode.java`, read-only) has an `int val` and the children `left` and `right`; an empty tree is `null`. Rearrange the nodes you are given: the tests check that every node keeps its own two children, swapped, and that the root you return is the root you were given. Trees below are written in level order: the values level by level, left to right, with `null` for a missing child and trailing `null`s left out. For example `[5,2,null,7]` is a root 5 whose left child 2 has a left child 7.

## Example 1

**Input:** `root = [6,3,9,1,4,8]`
**Output:** `[6,9,3,null,8,4,1]`
**Explanation:** The root's children 3 and 9 swap sides; below them, 3 now holds 4 then 1, and the 8 that hung on the left of 9 now hangs on its right.

## Example 2

**Input:** `root = [5,2,null,7]`
**Output:** `[5,null,2,null,7]`
**Explanation:** A tree that leans left all the way down leans right all the way down once mirrored.

## Example 3

**Input:** `root = []`
**Output:** `[]`
**Explanation:** An empty tree is its own mirror image, so the answer is `null`.

## Constraints

- `0 <= number of nodes <= 10^5`
- `-10^4 <= node value <= 10^4`
- `0 <= depth <= 1000`, where depth is the number of nodes on the longest path from the root down to a leaf

## Follow-up

Can you do it without recursion, so that the depth limit no longer matters?
