# Redundant Connection

A campus has `n` buildings numbered `0` to `n - 1`, joined by two-way network cables. The cables were first laid as a tree: every building could reach every other one, with exactly one route between any two. Then one more cable was added between two buildings that were not yet directly joined, so the campus now has exactly `n` cables and exactly one loop.

Implement `Cables.spare(int[][] cables)` in `Cables.java`. The array has `n` entries, and `cables[i] = [a, b]` joins buildings `a` and `b`. Return the cable whose removal turns the network back into a tree. Several cables may qualify (any cable on the loop does); return the one that appears last in `cables`, exactly as it is listed there, with its two ends in the same order.

Buildings are numbered from 0, and `n` is not passed separately: it is `cables.length`.

## Example 1

**Input:** `cables = [[3, 4], [0, 3], [4, 1], [1, 0], [2, 1]]`
**Output:** `[1, 0]`
**Explanation:** The loop is 0, 3, 4, 1 and back to 0. Removing any of its four cables leaves a tree, and `[1, 0]` is listed after the other three.

## Example 2

**Input:** `cables = [[0, 1], [1, 2], [1, 3], [3, 4], [4, 2], [0, 5]]`
**Output:** `[4, 2]`
**Explanation:** The loop is 1, 2, 4, 3. The cable `[0, 5]` comes later in the list, but removing it would cut building 5 off, so the answer is the loop cable `[4, 2]`.

## Example 3

**Input:** `cables = [[1, 0], [2, 1], [0, 2]]`
**Output:** `[0, 2]`
**Explanation:** Three buildings in a ring: every cable is on the loop, so the last one listed is the answer, given as `[0, 2]` and not reordered.

## Constraints

- `3 <= cables.length <= 10^5`
- `cables[i].length == 2`
- `0 <= cables[i][0], cables[i][1] < cables.length`
- `cables[i][0] != cables[i][1]`
- No two cables join the same pair of buildings.
- The cables connect every building, and removing one of them can leave a tree.

## Follow-up

Can you do it in close to linear time, without searching the network again for every cable?
