## Level 1
Think about what ends a balanced stretch. Which characters can never be part of any balanced stretch, and what do they do to the stretches on either side?

## Level 2
An unmatched `)` or an unmatched `(` cuts the string into pieces. If you could remember where the last such cut was, the length of a balanced stretch ending at the current position would be a difference of two positions.

## Level 3
Use a stack of positions instead of characters, with one extra position placed just before the string to stand for "the last cut". Work out what each `(` and each `)` should do to that stack, and what the top of the stack means right after a successful match. The length of a balanced stretch ending at position `i` is then `i` minus something on the stack.
