## Level 1

On a line nested a million deep, count how far back your code looks to find the bracket that a closer matches. A stack answers that question without looking back at all.

## Level 2

Searching backwards for the nearest unmatched opener costs up to the whole line for every closer, which is quadratic for deep nesting. With a stack, the match is always the top, so each character costs constant time. Watch memory too: a stack of boxed objects for a million entries is heavy; an array of ints is not.

## Level 3

1. Make sure `firstError` uses a stack of open columns and never scans backwards.
2. Consider an int array that doubles when full instead of a deque of boxed values.
3. Keep `isBalanced` defined through `firstError` so both stay fast.
4. Run Phase 3 tests and compare each test's time with its limit.
