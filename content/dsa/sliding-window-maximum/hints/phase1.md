## Level 1
Looking at every element of every window repeats a lot of work, because neighbouring windows share almost all their elements. What can you carry over from one window to the next?

## Level 2
When a new, larger element enters the window, every smaller element before it can never be a maximum again: it leaves the window earlier than the new one does. So you only need to keep the candidates that could still win.

## Level 3
Keep a deque of positions whose values are in decreasing order from front to back. For each new position: drop positions from the back while their value is not larger than the new value; add the new position at the back; drop the front position if it has left the window; once the first window is full, the front of the deque is the maximum.
