# Merge Two Sorted Lists

You are given the heads of two singly linked lists, `first` and `second`, each sorted in non-decreasing order. Join them into one list sorted in non-decreasing order and return its head.

Implement `MergeLists.merge(ListNode first, ListNode second)` in `MergeLists.java`. `ListNode` (in `ListNode.java`, read-only) has an `int val` and a `ListNode next`; an empty list is `null`. Build the result by relinking the given nodes: the tests check that every node of both lists appears exactly once and that no new node does. Lists below are written head first, as `[first, ..., last]`.

## Example 1

**Input:** `first = [2,5,9]`, `second = [3,4,10,12]`
**Output:** `[2,3,4,5,9,10,12]`
**Explanation:** The smaller front value is taken each time; once `first` runs out after 9, the rest of `second`, `[10,12]`, is attached as it is.

## Example 2

**Input:** `first = [-7,0,0]`, `second = [0,6]`
**Output:** `[-7,0,0,0,6]`
**Explanation:** Equal values from both lists all stay; three nodes hold 0.

## Example 3

**Input:** `first = []`, `second = [-3,-3]`
**Output:** `[-3,-3]`
**Explanation:** With one list empty, the answer is the other list unchanged.

## Constraints

- `0 <= number of nodes in first <= 10^5`
- `0 <= number of nodes in second <= 10^5`
- `-10^6 <= node value <= 10^6`
- Both lists are sorted in non-decreasing order.

## Follow-up

Can you do it with O(1) extra space?
