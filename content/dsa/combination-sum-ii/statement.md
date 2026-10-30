# Combination Sum II

You are given an array of positive whole numbers, `candidates`, which may hold the same value more than once, and a positive `target`. A combination is a selection of positions whose values add up to `target`, each position used at most once. Two combinations are the same when they hold the same values the same number of times, whichever positions those values came from.

Implement `CombinationSumOnce.find(int[] candidates, int target)` in `CombinationSumOnce.java`. Return every distinct combination exactly once, each as a list of its values. The values inside a combination may be in any order, and the combinations may be returned in any order. If there are none, return an empty list.

## Example 1

**Input:** `candidates = [4, 2, 6, 2, 3]`, `target = 8`
**Output:** `[[2, 6], [2, 2, 4]]` (in any order)
**Explanation:** Either 2 with the 6 makes 8, but that is one combination. Both 2s with the 4 make the other. The 3 would need 5 more, which no unused values give.

## Example 2

**Input:** `candidates = [5, 5, 5]`, `target = 10`
**Output:** `[[5, 5]]`
**Explanation:** Three different pairs of positions give two 5s; the combination is listed once.

## Example 3

**Input:** `candidates = [10, 20]`, `target = 15`
**Output:** `[]`
**Explanation:** 10 alone falls short, and any selection with 20 overshoots, so no combination exists.

## Constraints

- `1 <= candidates.length <= 80`
- `1 <= candidates[i] <= 40`
- `1 <= target <= 32`

## Follow-up

Can the work depend on the number of distinct combinations rather than on every choice of positions?
