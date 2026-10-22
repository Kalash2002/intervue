## Level 1
Testing every substring gives the right count, but the tests overlap: a long palindrome is a shorter one with a matching letter added at each end.

## Level 2
Every palindrome has exactly one middle. Starting from a middle and stepping outward one letter on each side at a time, which palindromes do you pass, and when can you stop?

## Level 3
A middle is either a letter or the gap between two letters, and each palindrome is counted once if you count, at every middle, how far it can grow.
