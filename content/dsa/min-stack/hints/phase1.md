## Level 1
A normal stack gives you push, pop and top for free. The hard part is the minimum after a pop: the value that was smallest may be the one that just left. What would you need to remember to get the previous minimum back?

## Level 2
The minimum at any moment depends only on what is currently in the stack, and the stack changes only at the top. So the minimum that applied just before a value was pushed is a fact about that moment. Could you store something next to each value?

## Level 3
The minimum of everything up to and including an entry never changes while that entry stays in the stack. Think about when it is cheapest to work it out.
