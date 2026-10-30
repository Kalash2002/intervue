# Minimum Interval to Include Each Query

A mobile network has radio zones along a highway. Each zone is a closed stretch `[from, to]` of kilometre posts: it covers every post from `from` to `to`, both included, so its size is `to - from + 1` posts. A phone at a post connects to the smallest zone that covers that post.

Implement `Coverage.smallestZone(int[][] zones, int[] points)` in `Coverage.java`. For each `points[j]`, the post a phone is at, find the size of the smallest zone that covers it, or `-1` if no zone does. Return the answers in an array, in the same order as `points`. Zones may overlap and nest, and points may repeat and come in any order.

## Example 1

**Input:** `zones = [[10,18],[14,15],[16,30],[25,27]]`, `points = [15,26,9,17]`
**Output:** `[2,3,-1,9]`
**Explanation:** Post 15 is covered by `[10,18]` (9 posts) and by `[14,15]` (2 posts), whose last post it is. Post 26 is covered by `[16,30]` and `[25,27]`, sizes 15 and 3. No zone covers post 9. Post 17 is covered by `[10,18]` and `[16,30]`, and the smaller has 9 posts.

## Example 2

**Input:** `zones = [[1,100],[40,60]]`, `points = [50,61,50]`
**Output:** `[21,100,21]`
**Explanation:** Post 61 is one past the end of `[40,60]`, so only the long zone covers it. The repeated point gets the same answer both times.

## Example 3

**Input:** `zones = [[7,7]]`, `points = [7,8]`
**Output:** `[1,-1]`
**Explanation:** A zone of a single post has size 1, and the post next to it is not covered.

## Constraints

- `1 <= zones.length <= 10^5`
- `1 <= points.length <= 10^5`
- `1 <= from <= to <= 10^9` for every zone
- `1 <= points[j] <= 10^9`

## Follow-up

What if each point had to be answered as it arrives, before the next one is known?
