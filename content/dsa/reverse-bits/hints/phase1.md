## Level 1
Write a word as all 32 of its bits, the leading zeros too, and mirror it by hand. If you go through a string, which conversion gives you those leading zeros, and which parse accepts a result whose top bit is 1?

## Level 2
Build the result one bit at a time: take the lowest bit of the input, push it in at the bottom of the result, and move both along by one. How many times must that happen, whatever the input is?

## Level 3
Swapping the two halves of the word, then the two halves of each half, and so on down to single bits, mirrors the whole word.
