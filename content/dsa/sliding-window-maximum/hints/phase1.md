## Level 1
Looking at every element of every window repeats a lot of work, because neighbouring windows share almost all their elements. What can you carry over from one window to the next?

## Level 2
When a new, larger element enters the window, every smaller element before it can never be a maximum again: it leaves the window earlier than the new one does. So you only need to keep the candidates that could still win.

## Level 3
Some elements in the window can be ruled out for good the moment a larger one arrives. Think about what order the survivors must be in.
