## Level 1
Values far below the k-th largest can never matter again: new values only push the k-th largest up. How many values do you actually need to keep?

## Level 2
Keep only the k largest values seen so far. When a new value comes, which one of those k is the one it might replace, and which one is the answer?

## Level 3
Among the k largest values, the answer is always the smallest of them, so you need a structure that hands you its smallest value cheaply.
