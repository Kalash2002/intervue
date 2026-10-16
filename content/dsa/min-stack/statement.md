# Min Stack

Design a stack of integers that can also tell you the smallest value currently in it, without searching.

Implement the class `MinStack` in `MinStack.java` with a no-argument constructor and these methods. Every method must run in constant time (amortized is fine for `push`).

- `void push(int value)` puts `value` on top of the stack.
- `void pop()` removes the value on top.
- `int top()` returns the value on top without removing it.
- `int getMin()` returns the smallest value currently in the stack.

Equal values can be pushed more than once. In the examples, the input is the sequence of calls on a new stack, and the output lists the values returned by the calls that return one, in order.

## Example 1

**Input:** `["push(4)", "push(7)", "getMin()", "top()", "pop()", "top()"]`
**Output:** `[4, 7, 4]`
**Explanation:** The smallest of 4 and 7 is 4, the top is 7, and after the pop the top is 4 again.

## Example 2

**Input:** `["push(5)", "push(2)", "push(8)", "pop()", "getMin()"]`
**Output:** `[2]`
**Explanation:** Removing the 8 does not change the smallest value, which is still 2.

## Example 3

**Input:** `["push(3)", "push(1)", "push(1)", "pop()", "getMin()"]`
**Output:** `[1]`
**Explanation:** One copy of 1 was popped, but the other is still in the stack, so the smallest value is still 1.

## Constraints

- `1 <= number of calls on one stack <= 5 * 10^5`
- `0 <= number of values in the stack at any moment <= 10^5`
- `-2^31 <= value <= 2^31 - 1`
- `pop`, `top` and `getMin` are only called when the stack holds at least one value.

## Follow-up

Can you keep the extra memory below one stored minimum per value?
