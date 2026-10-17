# Serialize and Deserialize Binary Tree

A service has to store binary trees as text and load them back later. Write both halves of that trip.

Implement the class `TreeCodec` in `TreeCodec.java` with two instance methods:

- `String serialize(TreeNode root)` turns the tree into a string. `root` may be `null`, the empty tree.
- `TreeNode deserialize(String data)` rebuilds the tree from a string that `serialize` returned.

For every tree, `deserialize(serialize(root))` must return a tree of new nodes with the same shape and the same value in every position. The format is yours: the tests never read the text, they only hand it back to `deserialize`. The one rule on the text is its size: for a tree of `n` nodes it may be at most `20 * (n + 1)` characters long. The two methods may be called on different `TreeCodec` instances, so the object must not remember anything between calls.

`TreeNode` (in `TreeNode.java`, read-only) has an `int val` and the children `left` and `right`. Trees below are written in level order: the values level by level, left to right, with `null` for a missing child and trailing `null`s left out. For example `[7,null,7]` is a root 7 whose right child is also 7.

## Example 1

**Input:** `root = [31,-6,4,120,null,null,-6]`
**Output:** `[31,-6,4,120,null,null,-6]` (the tree `deserialize(serialize(root))` returns)
**Explanation:** The tree comes back with the same shape and values. Any text works as long as it fits in `20 * 6 = 120` characters and `deserialize` can rebuild the tree from it; values may repeat, like the two -6s here.

## Example 2

**Input:** `root = [7,7]`, then `root = [7,null,7]`
**Output:** `[7,7]`, then `[7,null,7]`
**Explanation:** Both trees hold the same values in the same pre-order and in-order, but one child is on the left and the other on the right. The text has to record where the missing children are, or the two trees would come back the same.

## Example 3

**Input:** `root = []`
**Output:** `[]`
**Explanation:** The empty tree is `null`. `serialize(null)` must still return a string, at most 20 characters long, and `deserialize` must turn it back into `null`.

## Constraints

- `0 <= number of nodes <= 10^5`
- `-10^9 <= node value <= 10^9`
- `0 <= depth <= 1000`, where depth is the number of nodes on the longest path from the root down to a leaf
- `serialize` returns a string of at most `20 * (number of nodes + 1)` characters

## Follow-up

Can both methods run in time linear in the number of nodes?
