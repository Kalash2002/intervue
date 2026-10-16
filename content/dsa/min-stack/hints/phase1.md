## Level 1
A normal stack gives you push, pop and top for free. The hard part is the minimum after a pop: the value that was smallest may be the one that just left. What would you need to remember to get the previous minimum back?

## Level 2
The minimum at any moment depends only on what is currently in the stack, and the stack changes only at the top. So the minimum that applied just before a value was pushed is a fact about that moment. Could you store something next to each value?

## Level 3
Once a value is in the stack, the minimum of everything up to and including it never changes while it stays. So the answer to "what is the minimum now" can be worked out once, when each value is pushed, and removed together with it. Think about what to store when the new value equals the current minimum.
