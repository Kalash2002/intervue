# Linked List Cycle

You are given the head of a singly linked list. Return `true` if following `next` links from the head ever reaches a node that was already visited (the list loops back on itself), and `false` if the links end at `null`.

Implement `CycleCheck.hasCycle(ListNode head)` in `CycleCheck.java`. `ListNode` (in `ListNode.java`, read-only) has an `int val` and a `ListNode next`; an empty list is `null`. Do not change the list: the tests check every value and link afterwards. In the examples a list is written head first, `[first, ..., last]`, and `pos` is the index of the node the last node links back to, or `-1` when the last node's `next` is `null`. `pos` only describes how the input is built; your method receives just the head.

## Example 1

**Input:** `head = [5,1,8,2]`, `pos = 1`
**Output:** `true`
**Explanation:** The node holding 2 links back to the node holding 1, so the walk goes 5, 1, 8, 2, 1, 8, 2, ... forever.

## Example 2

**Input:** `head = [4,4,4]`, `pos = -1`
**Output:** `false`
**Explanation:** The walk sees the value 4 three times, but these are three different nodes and the last one links to `null`; equal values are not a loop.

## Example 3

**Input:** `head = [13]`, `pos = 0`
**Output:** `true`
**Explanation:** The only node links to itself, which is a cycle of length one.

## Constraints

- `0 <= number of nodes <= 10^5`
- `-10^6 <= node value <= 10^6`
- `-1 <= pos < number of nodes`

## Follow-up

Can you do it with O(1) extra space?
