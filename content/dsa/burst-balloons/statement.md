# Burst Balloons

A row of balloons has a number painted on each one. You burst them one at a time, in any order you like, until none is left. Bursting a balloon pays coins: its number times the numbers of its two neighbours, the nearest balloon still unburst on its left and the nearest on its right. A side with no balloon left on it counts as 1.

Implement `Balloons.mostCoins(int[] values)` in `Balloons.java`, where `values[i]` is the number on the i-th balloon from the left, and return the largest total that any order of bursting pays.

A burst pays at most 100 × 100 × 100 = 10^6 coins, so the total for 300 balloons is at most 3 × 10^8. That fits an `int`, and so does every total for part of the row along the way.

## Example 1

**Input:** `values = [2, 3, 7, 4, 2]`
**Output:** `133`
**Explanation:** Keep the 7 for last, so that it is a neighbour in all four other bursts: the 3 pays 2 × 3 × 7 = 42, the first 2 pays 1 × 2 × 7 = 14, the 4 pays 7 × 4 × 2 = 56, the last 2 pays 7 × 2 × 1 = 14, and the 7 pays 1 × 7 × 1 = 7, 133 in all.

## Example 2

**Input:** `values = [5, 0, 7]`
**Output:** `42`
**Explanation:** Burst the 0 first. It pays nothing, but it leaves the 5 and the 7 side by side: the 5 then pays 1 × 5 × 7 = 35 and the 7 pays 7. While the 0 still stands, bursting the 5 or the 7 next to it pays nothing.

## Example 3

**Input:** `values = [9]`
**Output:** `9`
**Explanation:** The only balloon has no neighbour on either side, so both sides count as 1: 1 × 9 × 1.

## Constraints

- `1 <= values.length <= 300`
- `0 <= values[i] <= 100`

## Follow-up

What if you also had to give an order of bursting that pays the most?
