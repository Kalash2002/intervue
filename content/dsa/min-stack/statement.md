# Min Stack

Design a stack of integers that can also tell you the smallest value currently in it, without searching.

Implement the class `MinStack` in `MinStack.java` with a no-argument constructor and these methods. Every method must take constant time.

- `void push(int value)` puts `value` on top of the stack.
- `void pop()` removes the value on top.
- `int top()` returns the value on top without removing it.
- `int getMin()` returns the smallest value currently in the stack.

`pop`, `top` and `getMin` are only called on a stack that holds at least one value. Values can be any `int`, and equal values can be pushed more than once.

## Examples

- Calls `push(4)`, `push(7)`, `getMin()`, `top()`, `pop()`, `top()` return `4`, `7`, then `4`: the smallest of 4 and 7 is 4, the top is 7, and after the pop the top is 4.
- Calls `push(5)`, `push(2)`, `push(8)`, `pop()`, `getMin()` return `2`: removing the 8 does not change the smallest value.
- Calls `push(3)`, `push(1)`, `push(1)`, `pop()`, `getMin()` return `1`: one copy of 1 is still in the stack.

## Constraints

- At most 500,000 calls on one stack, and at most 100,000 values in it at once
- Values range over the whole `int` type, from -2,147,483,648 to 2,147,483,647
