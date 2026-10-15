## Level 1
For one value from the first list, what do you have to find out about the second list, and how often are you asking it?

## Level 2
You ask "is this value in the other list" many times. A structure that answers that in one step turns a repeated scan into a single lookup, and it can also swallow repeats for you.

## Level 3
Put one list into a set. Go through the other list and collect each value found in the set, taking it out of the set as you collect it so it cannot be collected twice. Sort the collected values at the end. Sorting both lists and walking them together with two indexes is an alternative that needs no set.
