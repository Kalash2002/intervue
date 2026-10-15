## Level 1
Sorting the array makes runs easy to see. What does that cost, and what happens to the repeated values? Is there a way to avoid sorting at all?

## Level 2
A run begins at a number whose predecessor is not in the array. If you can tell in constant time whether a number is present, which numbers are worth starting a count from?

## Level 3
Put the values in a set. Only start counting from a value whose predecessor is absent, and then walk upward while the next value is present. Think about why each value is then visited a small, fixed number of times.
