## Level 1
A normal stack gives you push, pop and top for free. The hard part is the minimum after a pop: the value that was smallest may be the one that just left. What would you need to remember to get the previous minimum back?

## Level 2
The minimum at any moment depends only on what is currently in the stack, and the stack changes only at the top. So the minimum that applied just before a value was pushed is a fact about that moment. Could you store something next to each value?

## Level 3
The minimum can only change when a value is pushed or popped, so for each height of the stack the answer is fixed the moment that height is reached. Think about what that lets you avoid doing later.
