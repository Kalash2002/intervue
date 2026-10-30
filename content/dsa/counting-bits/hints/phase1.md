## Level 1
Counting the bits of each number on its own works. Now look at the table it gives you: how does the entry for 38 compare with the entry for 19, and the entry for 39 with the entry for 19?

## Level 2
When you reach `i`, every smaller entry is already filled in. Which smaller number has almost the same binary form as `i`, so that its entry tells you most of the answer?

## Level 3
`i` and `i >> 1` have the same bits apart from the lowest bit of `i`, and `i >> 1` comes earlier in the table.
