# Search Insert Position

An array of integers is sorted in ascending order. It may contain repeated values. Given a `target`, return the smallest index `i` such that `sorted[i] >= target`. When every element is smaller than `target`, return the length of the array.

Put differently, this is the position where `target` would have to go to keep the array sorted, placed before any equal elements already there. Implement `SortedSlots.insertAt(int[] sorted, int target)` in `SortedSlots.java`.

Each call must be fast on a very long array: a scan from the left will be too slow when there are many calls.

## Example 1

**Input:** `sorted = [10, 20, 20, 30, 50]`, `target = 20`
**Output:** `1`
**Explanation:** The first 20 is at index 1. The second 20 is not the answer, because the smallest index wins.

## Example 2

**Input:** `sorted = [10, 20, 20, 30, 50]`, `target = 25`
**Output:** `3`
**Explanation:** 25 is not in the array. It would go between 20 and 30, at index 3.

## Example 3

**Input:** `sorted = [10, 20, 20, 30, 50]`, `target = 60`
**Output:** `5`
**Explanation:** Every element is smaller than 60, so it goes after the last one, at the length of the array.

## Example 4

**Input:** `sorted = []`, `target = 7`
**Output:** `0`
**Explanation:** An empty array has only one place to put it.

## Constraints

- `0 <= sorted.length <= 10^6`
- `sorted[i] <= sorted[i + 1]` for every valid `i`
- `-2^31 <= sorted[i], target <= 2^31 - 1`

## Follow-up

How would you find the position after the last equal element instead?
