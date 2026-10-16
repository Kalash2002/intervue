## Level 1
A normal stack gives you push, pop and top for free. The hard part is the minimum after a pop: the value that was smallest may be the one that just left. What would you need to remember to get the previous minimum back?

## Level 2
The minimum can change only when a value is pushed or popped. What does that tell you about the minimum at each height of the stack?

## Level 3
Keep the minimum for each height alongside its entry, so that a pop restores the previous minimum without any search.
