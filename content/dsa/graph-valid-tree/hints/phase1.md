## Level 1
A tree has two properties: everything is connected, and there is no loop. Which of the two can you check without looking at the shape at all?

## Level 2
A tree over `n` valves has exactly `n - 1` pipes. That count alone is not enough: a loop in one place can use up the pipe that should have reached a valve somewhere else. With the count right, either one of the two properties implies the other.

## Level 3
With the count already right, read the pipes one at a time and ask whether each new pipe joins two valves that are already connected.
