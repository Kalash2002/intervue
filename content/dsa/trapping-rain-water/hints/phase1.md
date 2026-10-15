## Level 1
Look at one position on its own. What decides how high the water can stand above it? Try to say it in terms of what lies to its left and to its right.

## Level 2
The water above a position reaches the lower of two heights: the tallest bar to its left and the tallest bar to its right. Computing both maxima again for every position is slow. How could you avoid repeating that work?

## Level 3
The side with the lower tallest-bar-so-far decides the level, whatever the other side holds. Work in from both ends and, at each step, handle the side whose maximum is smaller. Think about why the other side's exact maximum does not matter yet.
