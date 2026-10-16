## Level 1
Merging two sorted lists is a single walk. With `k` lists, the next node of the result is always one of the `k` current fronts. How long does it take you to find the smallest front?

## Level 2
Scanning all fronts for every node costs `k` steps per node. Between two picks only one front changes. Which structure gives you the smallest of a changing set quickly?

## Level 3
Keep the fronts in a min-heap ordered by value, so each pick and each replacement costs log k.
