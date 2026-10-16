## Level 1
Think about what ends a balanced stretch. Which characters can never be part of any balanced stretch, and what do they do to the stretches on either side?

## Level 2
An unmatched `)` or an unmatched `(` cuts the string into pieces. If you could remember where the last such cut was, the length of a balanced stretch ending at the current position would be a difference of two positions.

## Level 3
A stack can hold positions as well as characters. Think about what is left beneath a bracket once it has been matched, and what that tells you about the stretch it just completed.
