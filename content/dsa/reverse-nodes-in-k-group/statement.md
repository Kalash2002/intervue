# Reverse Nodes in K-Group

You are given the head of a singly linked list and a positive integer `k`. Split the list, from the head, into groups of `k` consecutive nodes and reverse the order of the nodes inside every full group. If fewer than `k` nodes are left at the end, they keep their order. Return the head of the resulting list.

Implement `KGroup.reverse(ListNode head, int k)` in `KGroup.java`. `ListNode` (in `ListNode.java`, read-only) has an `int val` and a `ListNode next`. Relink the nodes you are given: the tests check that the result is made of the same node objects, in the new order, so swapping values between nodes does not count. Lists below are written head first, as `[first, ..., last]`.

## Example 1

**Input:** `head = [10,20,30,40,50,60,70]`, `k = 3`
**Output:** `[30,20,10,60,50,40,70]`
**Explanation:** The groups are 10, 20, 30 and 40, 50, 60, each turned around; 70 alone is a short group and stays last.

## Example 2

**Input:** `head = [5,1,8,3]`, `k = 2`
**Output:** `[1,5,3,8]`
**Explanation:** The length is a multiple of 2, so both pairs are swapped and nothing is left over.

## Example 3

**Input:** `head = [4,9,2]`, `k = 1`
**Output:** `[4,9,2]`
**Explanation:** A group of one node reversed is the same node, so the list does not change.

## Constraints

- `1 <= number of nodes <= 10^5`
- `1 <= k <= number of nodes`
- `-10^6 <= node value <= 10^6`

## Follow-up

Can you do it with O(1) extra space?
