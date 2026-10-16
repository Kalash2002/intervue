# Remove Nth Node From End of List

You are given the head of a singly linked list and a number `n`. Remove the node that is `n` places from the end of the list, counting the last node as 1, and return the head of the shortened list.

Implement `RemoveFromEnd.remove(ListNode head, int n)` in `RemoveFromEnd.java`. `ListNode` (in `ListNode.java`, read-only) has an `int val` and a `ListNode next`; an empty list is `null`. Unlink the node; the tests check that every other node is still the same object, in the same order. Lists below are written head first, as `[first, ..., last]`.

## Example 1

**Input:** `head = [6,11,2,19,30]`, `n = 2`
**Output:** `[6,11,2,30]`
**Explanation:** Counting from the end, 30 is first and 19 is second, so the node holding 19 is removed.

## Example 2

**Input:** `head = [3,6,9]`, `n = 3`
**Output:** `[6,9]`
**Explanation:** The third node from the end of a three-node list is the head, so the list now starts at 6.

## Example 3

**Input:** `head = [12]`, `n = 1`
**Output:** `[]`
**Explanation:** Removing the only node leaves an empty list, returned as `null`.

## Constraints

- `1 <= number of nodes <= 10^5`
- `-10^6 <= node value <= 10^6`
- `1 <= n <= number of nodes`

## Follow-up

Can you do it in a single pass over the list?
