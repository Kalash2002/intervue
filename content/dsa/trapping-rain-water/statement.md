# Trapping Rain Water

A row of bars of width 1 stands on flat ground, and `heights[i]` is the height of the bar at position `i`. After a heavy rain, water collects in the dips between the bars. The water above position `i` rises to a level equal to the lower of two heights: the tallest bar at or to the left of `i`, and the tallest bar at or to the right of `i`. The amount of water above position `i` is that level minus `heights[i]`. Water at the two ends of the row therefore never stays, because there the level is the bar's own height.

Implement `Basin.trapped(int[] heights)` in `Basin.java`. Return the total amount of water, in square units, that the row holds.

## Example 1

**Input:** `heights = [3, 0, 2, 0, 4]`
**Output:** `7`
**Explanation:** For positions 1, 2 and 3 the tallest bar on the left is 3 and the tallest on the right is 4, so their level is 3. They hold 3, 1 and 3 units, and positions 0 and 4 hold nothing.

## Example 2

**Input:** `heights = [4, 1, 1, 4]`
**Output:** `6`
**Explanation:** The level over both middle positions is 4, so each holds 3 units: a flat basin two wide and three deep.

## Example 3

**Input:** `heights = [1, 2, 3]`
**Output:** `0`
**Explanation:** Heights only go up, so at every position the lower of the two tallest bars is the bar itself, and nothing is held.

## Constraints

- `0 <= heights.length <= 100000`
- `0 <= heights[i] <= 10000`
- The answer fits in a 32-bit signed integer.

## Follow-up

Can you do it in O(n) time with O(1) extra memory?
