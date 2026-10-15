## Level 1
Checking every pair works, but it gets slow when the array is long. Ask what you would like to know about each number the moment you meet it.

## Level 2
For a value you are looking at, the partner you need is fixed: target minus that value. Can you find out quickly whether you have already passed it?

## Level 3
Walk the array once. Keep a map from each value seen so far to its position. Before storing the current value, look up target minus the current value; if it is there, you have both positions. Store afterwards, so a value never pairs with itself.
