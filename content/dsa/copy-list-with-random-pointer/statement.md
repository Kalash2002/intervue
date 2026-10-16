# Copy List with Random Pointer

You are given the head of a singly linked list in which every node has a second link, `random`, that points to any node of the same list or is `null`. Build a deep copy: a new list with one new node per original node, the same values, and `next` and `random` links that join the new nodes in the same shape as the old ones. Return the head of the copy.

Implement `CopyRandomList.copy(Node head)` in `CopyRandomList.java`. `Node` (in `Node.java`, read-only) has an `int val`, a `Node next` and a `Node random`; an empty list is `null`. No node of the copy may be a node of the input, and no link of the copy may point into the input. Leave the input list exactly as you found it: the tests check every value and link of it after the call.

A list is written head first as `[[val, randomIndex], ...]`, where `randomIndex` is the position (from 0) of the node that `random` points to, or `null`.

## Example 1

**Input:** `head = [[6,2],[1,null],[9,0],[4,3]]`
**Output:** `[[6,2],[1,null],[9,0],[4,3]]`
**Explanation:** The copy has four new nodes; the new 6 points at the new 9, the new 9 back at the new 6, and the new 4 at itself.

## Example 2

**Input:** `head = [[5,1],[5,0]]`
**Output:** `[[5,1],[5,0]]`
**Explanation:** Both nodes hold 5, so the value alone cannot say which node a link targets; each new node's `random` must point at the other new node.

## Example 3

**Input:** `head = []`
**Output:** `[]`
**Explanation:** The copy of an empty list (`null`) is an empty list.

## Constraints

- `0 <= number of nodes <= 2 * 10^5`
- `-10^4 <= node value <= 10^4`
- `0 <= randomIndex < number of nodes`, for each `random` that is not `null`

## Follow-up

Can you do it in O(n) time with O(1) extra space besides the new nodes?
