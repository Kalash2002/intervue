# Median of Two Sorted Arrays

You are given two arrays `a` and `b`, each sorted in non-decreasing order. Return the median of all their values taken together: the middle value when the total count is odd, and the average of the two middle values when it is even. Values may repeat, within an array and across the two.

Implement `SortedMedian.median(int[] a, int[] b)` in `SortedMedian.java`, returning a `double`. The tests call it thousands of times on arrays of a million values, so even a single pass over the values per call will be too slow.

## Example 1

**Input:** `a = [3, 8, 21]`, `b = [5, 10, 13, 30]`
**Output:** `10.0`
**Explanation:** Together the values are 3, 5, 8, 10, 13, 21, 30. There are seven, and the fourth is 10.

## Example 2

**Input:** `a = [-4, 6]`, `b = [1, 9]`
**Output:** `3.5`
**Explanation:** Together the values are -4, 1, 6, 9. The two middle values are 1 and 6, and their average is 3.5.

## Example 3

**Input:** `a = []`, `b = [7]`
**Output:** `7.0`
**Explanation:** One array may be empty; the median is then the other array's own median.

## Example 4

**Input:** `a = [2147483647]`, `b = [2147483647]`
**Output:** `2147483647.0`
**Explanation:** The two middle values are both the largest `int`. Adding them as `int`s would overflow, but their average is the value itself.

## Constraints

- `0 <= a.length <= 10^6`
- `0 <= b.length <= 10^6`
- `1 <= a.length + b.length`
- `-2^31 <= a[i], b[i] <= 2^31 - 1`
- `a` and `b` are each sorted in non-decreasing order.

## Follow-up

Can you do it in O(log(min(m, n))) time, where m and n are the two lengths?
