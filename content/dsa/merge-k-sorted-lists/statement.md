# Merge K Sorted Lists

You are given an array of `k` singly linked lists, each sorted in non-decreasing order. Join all of them into one list sorted in non-decreasing order and return its head.

Implement `MergeK.merge(ListNode[] lists)` in `MergeK.java`. `ListNode` (in `ListNode.java`, read-only) has an `int val` and a `ListNode next`; an empty list is `null`, and the array itself may be empty. Build the result by relinking the given nodes: the tests check that every node of every list appears exactly once and that no new node does. Lists below are written head first, as `[first, ..., last]`.

## Example 1

**Input:** `lists = [[4,7,12],[1,9],[5,6,15]]`
**Output:** `[1,4,5,6,7,9,12,15]`
**Explanation:** The smallest front of the three lists is taken each time: 1, then 4, then 5, and so on until every list is used up.

## Example 2

**Input:** `lists = [[],[-2,3],[],[0]]`
**Output:** `[-2,0,3]`
**Explanation:** Empty lists contribute nothing; the two non-empty ones interleave.

## Example 3

**Input:** `lists = [[],[6,6],[]]`
**Output:** `[6,6]`
**Explanation:** Only one list has nodes, so the result is that list as it is, repeated value included.

## Constraints

- `0 <= k <= 5 * 10^4`
- `0 <= total number of nodes <= 2 * 10^5`
- `-10^6 <= node value <= 10^6`
- Each list is sorted in non-decreasing order.

## Follow-up

Can you do it in O(N log k) time, where N is the total number of nodes?
