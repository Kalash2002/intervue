## Level 1
Reading every star the same way, as `(`, as `)` or as nothing, fails on strings whose stars need different readings. A single left-to-right count that lets a star stand in for a missing `(` can still leave a `(` with nothing after it to close it. Trying all three readings of every star is correct but triples the work with each star. Think about what you need to know after each character.

## Level 2
After a prefix, different readings of its stars leave different numbers of brackets open. Which numbers are possible, and what does the set of them look like as you read on?

## Level 3
Two numbers are enough to carry along the string: the fewest and the most brackets that can be open.
