## Level 1
Checking every group of three is slow. If you fix one value, what is left to find, and which problem have you seen that does it faster?

## Level 2
Sorting first helps twice: it makes a pair search possible with two moving positions, and it puts equal values next to each other. Where do duplicates cause repeated triples, and how could you skip them?

## Level 3
Sort, then for each first value look for a pair that sums to its negative, using two positions on the rest of the array. Think about exactly where equal values would make the same triple appear twice.
