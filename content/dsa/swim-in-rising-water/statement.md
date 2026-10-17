# Swim in Rising Water

A square basin is divided into an `n` by `n` grid, and `elevation[r][c]` is the height of the floor in row `r`, column `c`. Rain starts at time 0 and raises the water evenly: at time `t` the water stands at level `t` everywhere. A cell is under water at time `t` when its elevation is at most `t`.

A swimmer starts in the top-left cell `(0, 0)` and wants to reach the bottom-right cell `(n - 1, n - 1)`. The swimmer can only be in a cell that is under water, so they wait in the first cell until it is covered. From a cell under water they can move to any of the four cells that share a side with it, if that cell is under water too, and moving takes no time, however far they go.

Implement `Pool.earliestCrossing(int[][] elevation)` in `Pool.java` and return the earliest time at which the swimmer can be in the bottom-right cell. Unlike some versions of this puzzle, elevations may repeat.

## Example 1

**Input:** `elevation = {{1,8,3},{6,9,2},{7,5,4}}`
**Output:** `7`
**Explanation:** At time 7 the swimmer goes down the left column through 1, 6 and 7, then right through 5 and 4. Every route has to step on 7, 8 or 9, because those surround the cells 1 and 6 that the swimmer can reach earlier.

## Example 2

**Input:** `elevation = {{3,3,9},{9,3,9},{9,3,3}}`
**Output:** `3`
**Explanation:** At time 3 the five cells of height 3 are under water and connect the two corners.

## Example 3

**Input:** `elevation = {{6}}`
**Output:** `6`
**Explanation:** The start is also the goal, but the swimmer can only be there once the water covers it, at time 6.

## Constraints

- `n == elevation.length == elevation[i].length`
- `1 <= n <= 300`
- `0 <= elevation[i][j] <= 10^6`

## Follow-up

How would you also return the route the swimmer takes?
