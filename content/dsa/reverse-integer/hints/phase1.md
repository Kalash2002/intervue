## Level 1
Peel the digits off the end of `x` one at a time with `% 10` and `/ 10`, and build the result from them. In what order do the digits come out, and what does Java's `%` give when `x` is negative?

## Level 2
The input always fits in an `int`, but its reverse may not, and in `int` arithmetic a step past the limit wraps around without any error. At which point of the loop can you tell that the next step would leave the range, before taking it?

## Level 3
Compare the result so far with `Integer.MAX_VALUE / 10` before you multiply it by 10, not the product afterwards.
