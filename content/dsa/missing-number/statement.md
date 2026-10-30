# Missing Number

A roll of raffle tickets was numbered from 0 to n, and exactly one ticket has gone missing. You are given the numbers of the n tickets that are left, in no particular order, in an array `nums`. Return the number of the missing ticket. It can be any of 0 to n, including 0 and n itself.

Implement `LostTicket.find(int[] nums)` in `LostTicket.java`.

## Example 1

**Input:** `nums = [4, 7, 1, 0, 6, 2, 3]`
**Output:** `5`
**Explanation:** Seven tickets are left, so the numbers ran from 0 to 7. Every one of them is in the array except 5.

## Example 2

**Input:** `nums = [3, 5, 1, 4, 2]`
**Output:** `0`
**Explanation:** n is 5 and the numbers 1 to 5 are all there, so the missing one is the smallest, 0.

## Example 3

**Input:** `nums = [0]`
**Output:** `1`
**Explanation:** With one ticket left, n is 1 and the numbers were 0 and 1. 0 is there, so the missing one is n itself, one past the last index of the array.

## Constraints

- `1 <= n <= 10^5`, where `n` is `nums.length`
- `0 <= nums[i] <= n`
- The values in `nums` are distinct.

## Follow-up

Can you find it in O(n) time with only a constant amount of extra memory, without changing `nums`?
