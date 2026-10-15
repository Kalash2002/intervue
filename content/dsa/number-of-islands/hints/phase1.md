## Level 1
Pick any land cell you have not looked at yet. Which other land cells are part of its island, and how would you find all of them without missing any?

## Level 2
From one land cell, keep visiting its land neighbours, then their land neighbours, until nothing new turns up. Every cell reached that way is one island. What do you need to remember so that no cell is counted twice?

## Level 3
Scan the grid cell by cell. Each time you meet land that has not been visited, that is a new island: add one, then explore outward from it and mark everything you reach. Prefer a loop with your own stack or queue to deep recursion, since one island can be a million cells long.
