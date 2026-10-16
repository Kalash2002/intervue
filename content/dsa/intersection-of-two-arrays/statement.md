# Intersection of Two Arrays

Two friends each export the track numbers of their favourite songs. A track can appear several times in an export. You want the track numbers that are in both exports.

Implement `Playlists.shared(int[] mine, int[] yours)` in `Playlists.java`. Return an `int[]` with each track number that occurs in both arrays, listed once, in ascending order. If no track is shared, return an empty array.

## Example 1

**Input:** `mine = [7, 3, 9, 3]`, `yours = [3, 8, 3, 7, 7]`
**Output:** `[3, 7]`
**Explanation:** Both 3 and 7 are in each list, and each is listed once however often it repeats. The 9 and the 8 are each in only one list.

## Example 2

**Input:** `mine = [-5, 0, 5]`, `yours = [5, -5, -6]`
**Output:** `[-5, 5]`
**Explanation:** Negative numbers are ordinary values, and ascending order puts -5 before 5.

## Example 3

**Input:** `mine = []`, `yours = [4, 4]`
**Output:** `[]`
**Explanation:** An empty list shares nothing with any other list, so the result is empty.

## Constraints

- `0 <= mine.length, yours.length <= 10^5`
- `-2^31 <= mine[i], yours[i] <= 2^31 - 1`

## Follow-up

What if both lists are already sorted?
