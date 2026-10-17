## Level 1
The best plan never contains a loop: removing any cable of a loop keeps everything joined and saves length. So the answer is a tree over the sensors. Which tree?

## Level 2
Grow the network from one sensor. At every step, some cable joins a sensor already in the network to one that is not, and the shortest such cable is always safe to take. The question is how to find it without comparing every pair each time.

## Level 3
For each sensor not yet joined, remember only the shortest cable from it to anything already in the network.
