# Walls and Gates

A floor plan is a grid of cells. A cell holds `-1` for a wall, `0` for an exit, or `2147483647` (that is, `Integer.MAX_VALUE`) for an open room whose distance is not known yet; the examples write that value as `INF`. One step moves to a cell that shares a side with the current one and is not a wall.

Implement `FloorPlan.fillDistances(int[][] rooms)` in `FloorPlan.java`. Change the grid in place so that every open room holds the fewest steps from it to any exit. A room from which no exit can be reached keeps `2147483647`. Walls and exits keep their values.

## Example 1

**Input:** `rooms = [[0,INF,INF,-1],[-1,-1,INF,INF],[INF,INF,INF,0]]`
**Output:** `[[0,1,2,-1],[-1,-1,2,1],[3,2,1,0]]`
**Explanation:** The top row is closest to the exit in the top left corner, and everything else is closest to the exit in the bottom right corner. The room in the bottom left corner is six steps from the top exit, around the walls, but only three from the exit in the bottom right.

## Example 2

**Input:** `rooms = [[INF,-1,0],[-1,INF,INF]]`
**Output:** `[[INF,-1,0],[-1,2,1]]`
**Explanation:** The room in the top left corner is boxed in by walls, so it keeps `INF`.

## Example 3

**Input:** `rooms = [[INF,INF]]`
**Output:** `[[INF,INF]]`
**Explanation:** With no exit on the plan, no room has a distance.

## Constraints

- `1 <= rooms.length <= 1000`
- `1 <= rooms[i].length <= 1000`
- All rows have the same length.
- Every `rooms[i][j]` is `-1`, `0` or `2147483647`.

## Follow-up

Can you do it in time proportional to the size of the plan, however many exits there are?
