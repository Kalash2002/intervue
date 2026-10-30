# Grader notes: Min Stack

Tests drive sequences of calls on a `MinStack` and check each answer. They cover: the sample, one element, the minimum rising back when the smallest value is popped, a duplicated minimum (push 3, 1, 1, pop, then `getMin` must still be 1; a second stack that is pushed to only on a strictly smaller value fails here), falling and rising pushes, extreme values (pushing `Integer.MAX_VALUE` and then `Integer.MIN_VALUE`; a version that stores differences from the minimum overflows and fails), two stacks that must not share state, and a large run of 100,000 pushes, then 100,000 pops, with a `getMin` after each call, checked against a minimum tracked in the test.

The naive solution scans the whole stack in `getMin`; with 100,000 values in the stack and a `getMin` after each call, that is billions of steps over boxed values, which times out at the limit and at 3x. The reference stores a minimum beside each value.
