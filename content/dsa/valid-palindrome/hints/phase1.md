## Level 1
Doing it by hand, where would you look first to find a mismatch? Compare it with building a cleaned-up copy and reversing it.

## Level 2
You only ever need to compare the outermost pair of characters that still count, then move inwards. How do you deal with a character at either end that you are supposed to ignore?

## Level 3
Keep one position at each end and move them towards each other. Skip a position whenever its character is not a letter or a digit, and compare the two that remain without regard to case. Decide what the loop condition must be so the middle is handled correctly.
