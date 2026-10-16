# Add Two Numbers

Two non-negative whole numbers are stored as singly linked lists of decimal digits, one digit per node, with the lowest digit at the head. Add the numbers and return their sum as a list of the same form.

Implement `AddNumbers.add(ListNode first, ListNode second)` in `AddNumbers.java`. `ListNode` (in `ListNode.java`, read-only) has an `int val` and a `ListNode next`. Leave both input lists unchanged: the tests check every digit and link of them after the call. Lists below are written head first, so `[7,3,2]` is the number 237.

## Example 1

**Input:** `first = [7,3,2]`, `second = [5,9]`
**Output:** `[2,3,3]`
**Explanation:** 237 + 95 = 332, written lowest digit first.

## Example 2

**Input:** `first = [6,1]`, `second = [4,8]`
**Output:** `[0,0,1]`
**Explanation:** 16 + 84 = 100: both columns carry, and the last carry becomes a digit of its own.

## Example 3

**Input:** `first = [0]`, `second = [8,4,6]`
**Output:** `[8,4,6]`
**Explanation:** The number 0 is the one-node list `[0]`; adding it changes nothing, and the lists have different lengths.

## Constraints

- `1 <= number of nodes in each list <= 10^4`
- `0 <= node value <= 9`
- The last node of a list holds 0 only when the whole list is `[0]`.

## Follow-up

The numbers can have thousands of digits. Can you add them in one pass without converting them to a numeric type?
