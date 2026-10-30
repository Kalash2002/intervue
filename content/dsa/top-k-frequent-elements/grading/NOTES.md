# Grader notes: Top K Frequent Elements

Tests cover: the sample, a single element, a tie-free answer with negative values, k equal to the number of distinct values, all values equal, the top value at the end of the array, large values, and a large array of 200,000 elements with five heavy values among about 185,000 values that appear once (shuffled, seeded). The tests sort the answer before comparing.

The naive solution counts each distinct value by scanning the whole array for it, which is quadratic when there are many distinct values; it times out on the large test at the limit and at 3x. The reference counts with a hash map and picks with buckets.
