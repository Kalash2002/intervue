## Level 1
Think about what ends a balanced stretch. Which characters can never be part of any balanced stretch, and what do they do to the stretches on either side?

## Level 2
An unmatched `)` or an unmatched `(` cuts the string into pieces. If you could remember where the last such cut was, the length of a balanced stretch ending at the current position would be a difference of two positions.

## Level 3
With one kind of bracket, two counters can stand in for any stack. Think about when to reset them, and what a pass in only one direction would miss.
