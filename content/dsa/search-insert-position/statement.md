# Search Insert Position

An array of integers is sorted in ascending order. It may contain repeated values. Given a `target`, return the smallest index `i` such that `sorted[i] >= target`. When every element is smaller than `target`, return the length of the array.

Put differently, this is the position where `target` would have to go to keep the array sorted, placed before any equal elements already there. Implement `SortedSlots.insertAt(int[] sorted, int target)` in `SortedSlots.java`.

Each call must be fast on a very long array: a scan from the left will be too slow when there are many calls.

## Examples

- `sorted = [10, 20, 20, 30, 50]`, `target = 20` returns `1`: the first 20 is at index 1.
- `sorted = [10, 20, 20, 30, 50]`, `target = 25` returns `3`: 25 would go between 20 and 30.
- `sorted = [10, 20, 20, 30, 50]`, `target = 5` returns `0` and `target = 60` returns `5`.

## Constraints

- 0 <= `sorted.length` <= 1,000,000
- `sorted` is in ascending order (equal neighbours allowed).
- `target` and every element are any `int` value.
