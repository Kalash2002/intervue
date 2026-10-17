# Grader notes: Swim in Rising Water

Deviation from the classic version: elevations may repeat and range from 0 to 10^6, instead of being a permutation of 0 to n² - 1. The statement says so.

Tests cover: the sample; a start that is the highest cell and a goal that is the highest cell (both corners must be under water; a solution that starts its region at time 0 without counting the start fails the first); one-cell and flat grids; a 5 by 5 grid whose only low route snakes right, down, left, down and right (a DP that only moves right and down returns 30 instead of 16); a low pocket next to the start that leads nowhere, with the way out through 9s; 300 seeded random grids of 1 to 6 cells a side with heights 0 to 20, so heights repeat, checked against a flood fill at every height in the test; and two large grids.

The large tests have a limit of 1,000 ms each (3x is 3,000 ms):
- `aLongWindingChannel`: a 297 by 297 grid with a one-cell-wide channel snaking through walls of 10^6, 44,401 cells long, its floor rising by 20 per cell and the goal at 0, so the answer, 887,980, is the cell before the goal.
- `aWallWithOneGap`: a 300 by 300 grid of seeded random heights up to 500,000, with a wall of heights 600,000 to 10^6 across row 150 and a single gap at 550,000, the answer; both corners are 0.

Timing on a cold JVM (a scratch harness that builds the same grids, outside the kit): the reference, Dijkstra keyed on the highest cell, solved them in 19 to 36 ms. The naive solution tries every time from the higher corner upwards with a fresh breadth-first search; both corners are low, so it faces hundreds of thousands of flood fills and times out on both tests, also at 3x. A faster-looking variant that tries only the heights present in the grid, in increasing order, still took 15.4 s and 17.6 s in the harness. The walkthrough's Better, binary search on the time with a breadth-first search, took 22 to 41 ms: it is within a logarithmic factor of the reference, and the tests deliberately do not separate them.
