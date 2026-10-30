## Level 1
Each digit contributes exactly one letter, chosen from its key, and the choices for different digits do not affect each other. How many strings does that give, and how would you list them by hand for two digits?

## Level 2
Choose a letter for the first digit, then for the second, and so on, keeping the letters chosen so far in one builder. When every digit has a letter, you hold one complete string. What must you undo before trying the next letter for the same digit?

## Level 3
The choices form a tree with one level per digit and one branch per letter on that digit's key; its leaves are exactly the strings to return.
