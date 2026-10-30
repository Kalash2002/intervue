# Combination Sum

You are given an array of distinct positive whole numbers, `candidates`, and a positive `target`. A combination is a list of candidate values that adds up to `target`, where any value may be used as many times as you like. Two combinations are the same when they use each value the same number of times, so `[2, 2, 5]` and `[5, 2, 2]` are one combination.

Implement `CombinationSum.find(int[] candidates, int target)` in `CombinationSum.java`. Return every distinct combination exactly once, each as a list of its values. The values inside a combination may be in any order, and the combinations may be returned in any order. If there are none, return an empty list.

## Example 1

**Input:** `candidates = [3, 5, 4]`, `target = 11`
**Output:** `[[3, 4, 4], [3, 3, 5]]` (in any order)
**Explanation:** 3 + 4 + 4 = 11 and 3 + 3 + 5 = 11. No other way of repeating 3, 4 and 5 reaches 11.

## Example 2

**Input:** `candidates = [6, 2, 9]`, `target = 12`
**Output:** `[[6, 6], [2, 2, 2, 6], [2, 2, 2, 2, 2, 2]]` (in any order)
**Explanation:** 6 can be used twice, and 2 as often as needed. 9 leaves 3, which no mix of 2 and 6 can make.

## Example 3

**Input:** `candidates = [8, 12]`, `target = 5`
**Output:** `[]`
**Explanation:** Every candidate is larger than the target, so not even one value fits.

## Constraints

- `1 <= candidates.length <= 30`
- `2 <= candidates[i] <= 40`
- All values in `candidates` are distinct.
- `1 <= target <= 40`

## Follow-up

What if you only needed the number of combinations, not the combinations themselves?
