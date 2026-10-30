## Level 1
Two separate jobs hide here: finding out how often each value occurs, and then picking the k biggest counts. Solve the counting well first.

## Level 2
A hash map from value to count gives all the counts in one pass. For the picking, you could sort the distinct values by count, but a structure that keeps only the best k so far can do better.

## Level 3
A count can never exceed the length of the array, so counts themselves can be positions in an array. Think about what you could store at each position.
