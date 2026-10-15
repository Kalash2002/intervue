# Intersection of Two Arrays

Two friends each export the track numbers of their favourite songs. A track can appear several times in an export. You want the track numbers that are in both exports.

Implement `Playlists.shared(int[] mine, int[] yours)` in `Playlists.java`. Return an `int[]` with each track number that occurs in both arrays, listed once, in ascending order. If no track is shared, return an empty array.

## Examples

- `mine = {7,3,9,3}`, `yours = {3,8,3,7,7}` returns `{3,7}`: both 3 and 7 are in each list, and each is listed once however often it repeats.
- `mine = {1,2,3}`, `yours = {4,5,6}` returns `{}`: nothing is shared.
- `mine = {-5,0,5}`, `yours = {5,-5,-6}` returns `{-5,5}`: negative numbers are ordinary values.

## Constraints

- 0 <= `mine.length`, `yours.length` <= 100,000
- Every track number fits in an `int`; negatives are allowed.
