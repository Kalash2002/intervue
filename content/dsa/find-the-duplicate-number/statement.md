# Find the Duplicate Number

You are given an array `nums` of `n + 1` integers, each between 1 and `n` inclusive. Since there are more values than possible distinct values, at least one value repeats; here exactly one value repeats, possibly more than twice, and some values from 1 to `n` may be missing. Return the value that repeats.

Implement `FindDuplicate.find(int[] nums)` in `FindDuplicate.java`. Do not change `nums`: the tests compare it with a copy after the call. Aim for O(1) extra space.

## Example 1

**Input:** `nums = [3,5,4,1,2,4]`
**Output:** `4`
**Explanation:** Here `n = 5`; every value from 1 to 5 appears once except 4, which appears twice.

## Example 2

**Input:** `nums = [2,6,2,1,2,5,2]`
**Output:** `2`
**Explanation:** The value 2 appears four times, and 3 and 4 do not appear at all.

## Example 3

**Input:** `nums = [1,1]`
**Output:** `1`
**Explanation:** With `n = 1` the only allowed value is 1, so it is the duplicate.

## Constraints

- `1 <= n <= 2 * 10^5`
- `nums.length == n + 1`
- `1 <= nums[i] <= n`
- Exactly one value appears more than once.

## Follow-up

Can you find it in O(n) time with O(1) extra space and without writing to `nums`?
