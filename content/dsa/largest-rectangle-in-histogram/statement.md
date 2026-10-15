# Largest Rectangle in Histogram

A skyline is drawn as bars of width 1 standing side by side on a flat base, and `heights[i]` is the height of the bar at position `i`. A rectangle inside the skyline sits on the base, spans one or more neighbouring bars, and may be no taller than the shortest bar it spans.

Implement `Skyline.biggestBlock(int[] heights)` in `Skyline.java`. Return the area, in square units, of the largest such rectangle. An empty skyline, or one where every bar has height 0, has area 0.

## Examples

- `heights = [3, 1, 3, 2, 2]` returns `6`: the last three bars (heights 3, 2, 2) allow a rectangle 3 wide and 2 tall.
- `heights = [4, 4, 1, 3]` returns `8`: the first two bars give a rectangle 2 wide and 4 tall.
- `heights = [7]` returns `7`: a single bar is a rectangle of width 1.

## Constraints

- 0 <= `heights.length` <= 100,000
- 0 <= `heights[i]` <= 10,000
- The answer always fits in a 32-bit `int`
