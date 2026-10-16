# Largest Rectangle in Histogram

A skyline is drawn as bars of width 1 standing side by side on a flat base, and `heights[i]` is the height of the bar at position `i`. A rectangle inside the skyline sits on the base, spans one or more neighbouring bars, and may be no taller than the shortest bar it spans.

Implement `Skyline.biggestBlock(int[] heights)` in `Skyline.java`. Return the area, in square units, of the largest such rectangle. An empty skyline, or one where every bar has height 0, has area 0.

## Example 1

**Input:** `heights = [3, 1, 3, 2, 2]`
**Output:** `6`
**Explanation:** The last three bars have heights 3, 2 and 2, so a rectangle 3 wide and 2 tall fits over them. No other rectangle is larger.

## Example 2

**Input:** `heights = [4, 4, 1, 3]`
**Output:** `8`
**Explanation:** The first two bars allow a rectangle 2 wide and 4 tall. Spanning all four bars would be limited to height 1, which gives only 4.

## Example 3

**Input:** `heights = [7]`
**Output:** `7`
**Explanation:** A single bar is a rectangle of width 1.

## Example 4

**Input:** `heights = []`
**Output:** `0`
**Explanation:** With no bars there is no rectangle, so the area is 0.

## Constraints

- `0 <= heights.length <= 10^5`
- `0 <= heights[i] <= 10^4`
- The area always fits in a 32-bit `int`, since it is at most `10^9`.

## Follow-up

Can you beat O(n²) time?
