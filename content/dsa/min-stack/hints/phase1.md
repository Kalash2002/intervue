## Level 1
A normal stack gives you push, pop and top for free. The hard part is the minimum after a pop: the value that was smallest may be the one that just left. What would you need to remember to get the previous minimum back?

## Level 2
The minimum at any moment depends only on what is currently in the stack, and the stack changes only at the top. So the minimum that applied just before a value was pushed is a fact about that moment. Could you store something next to each value?

## Level 3
Alongside each value, store the smallest value in the stack up to and including that entry. Then getMin reads the top entry, and a pop discards both. Think about what you store when the new value equals the current minimum.
