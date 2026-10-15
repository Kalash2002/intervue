## Level 1
Two separate jobs hide here: finding out how often each value occurs, and then picking the k biggest counts. Solve the counting well first.

## Level 2
A hash map from value to count gives all the counts in one pass. For the picking, you could sort the distinct values by count, but a structure that keeps only the best k so far can do better.

## Level 3
Count with a map. Then either sort the distinct values by count and take the first k, or build buckets: an array indexed by count whose slot holds the values with that count, and read it from the highest count down until you have k values. The array length is at most the input length plus one, so this is linear.
