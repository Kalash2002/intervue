# Two Sum II: Sorted Input

You are given an array of whole numbers in non-decreasing order, and a target. Exactly one pair of different positions has values that add up to the target.

Implement `SortedPair.find(int[] numbers, int target)` in `SortedPair.java`. Return an array `{i, j}` with `i < j` and `numbers[i] + numbers[j] == target`. Positions count from 0. A position cannot be used twice, but two positions may hold the same value.

## Example 1

**Input:** `numbers = [-4, 2, 3, 9, 12, 17]`, `target = 21`
**Output:** `[3, 4]`
**Explanation:** The values at positions 3 and 4 are 9 and 12, and 9 + 12 = 21. No other pair adds up to 21.

## Example 2

**Input:** `numbers = [1, 4, 4, 9]`, `target = 8`
**Output:** `[1, 2]`
**Explanation:** The two 4s sit at different positions, so they form a valid pair.

## Example 3

**Input:** `numbers = [3, 5, 7]`, `target = 10`
**Output:** `[0, 2]`
**Explanation:** 5 + 5 would use one position twice, so it is not allowed. The pair is 3 + 7.

## Constraints

- `2 <= numbers.length <= 200000`
- `-10^8 <= numbers[i] <= 10^8`
- `numbers` is in non-decreasing order.
- `-2 * 10^8 <= target <= 2 * 10^8`
- Exactly one pair of positions adds up to `target`.

## Follow-up

Can you find the pair in O(n) time using only O(1) extra memory?
