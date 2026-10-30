## Level 1
Each value is either in a subset or out of it, and that choice is made once per value. How many subsets does that give for n values, and in what order could you list them so none is missed?

## Level 2
Go through the values one at a time and make the in-or-out choice for each. When every value has been decided you hold one complete subset. What must you undo after exploring one choice so the other choice starts from the same place?

## Level 3
The choices form a tree with two branches per value, and its leaves are exactly the subsets, so one list of chosen values is enough for the whole walk if every leaf saves a copy of it.
