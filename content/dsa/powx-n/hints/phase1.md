## Level 1
Multiplying `x` in one factor at a time is right, but `n` can be about two billion. How could you get `x^8` with fewer than eight multiplications?

## Level 2
`x^n` is `(x^(n/2))^2`, times one more `x` when `n` is odd, so each step can halve the exponent. And before you negate a negative `n`: what is `-n` when `n` is the smallest `int`?

## Level 3
Write `n` in binary: `x^n` is the product of the squares `x`, `x^2`, `x^4`, ... whose bit is set.
