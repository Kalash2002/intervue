# Reorder List

You are given the head of a singly linked list and rearrange it in place so that it alternates from both ends: the first node, then the last, then the second, then the second-to-last, and so on until every node is used once.

Implement `ReorderList.reorder(ListNode head)` in `ReorderList.java`; it returns nothing, and the tests read the list from the same `head` afterwards. `ListNode` (in `ListNode.java`, read-only) has an `int val` and a `ListNode next`. Move the nodes themselves: the tests check node identity, so rewriting the values does not count. Lists below are written head first, as `[first, ..., last]`.

## Example 1

**Input:** `head = [10,20,30,40,50]`
**Output:** `[10,50,20,40,30]`
**Explanation:** First 10, then the last node 50, then 20, then 40; the middle node 30 is left over and ends the list.

## Example 2

**Input:** `head = [7,1,7,3]`
**Output:** `[7,3,1,7]`
**Explanation:** The front nodes 7 and 1 alternate with the back nodes 3 and 7. The two 7s are different nodes: the first stays at the head, the second ends the list.

## Example 3

**Input:** `head = [9,2]`
**Output:** `[9,2]`
**Explanation:** With two nodes the first is followed by the last, which is already the order, so nothing moves.

## Constraints

- `1 <= number of nodes <= 2 * 10^5`
- `-10^6 <= node value <= 10^6`

## Follow-up

Can you do it in O(n) time with O(1) extra space?
