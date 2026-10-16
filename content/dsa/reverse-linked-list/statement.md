# Reverse Linked List

You are given the head of a singly linked list. Reverse the list so that the last node comes first and every link points the other way, and return the new head.

Implement `ReverseList.reverse(ListNode head)` in `ReverseList.java`. `ListNode` (in `ListNode.java`, read-only) has an `int val` and a `ListNode next`; an empty list is `null`. Relink the nodes you are given: the tests check that the returned list is made of the same node objects, in reverse order. Lists below are written head first, as `[first, ..., last]`.

## Example 1

**Input:** `head = [8,3,5,1]`
**Output:** `[1,5,3,8]`
**Explanation:** The node holding 1 was last and is now the head; each node now links to the one that used to come before it.

## Example 2

**Input:** `head = [6,-2]`
**Output:** `[-2,6]`
**Explanation:** With two nodes, the second becomes the head and the first becomes the tail, whose `next` is now `null`.

## Example 3

**Input:** `head = []`
**Output:** `[]`
**Explanation:** An empty list (`null`) has nothing to reverse, so the answer is `null`.

## Constraints

- `0 <= number of nodes <= 10^5`
- `-10^6 <= node value <= 10^6`

## Follow-up

Can you do it in one pass with O(1) extra space?
