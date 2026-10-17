## Level 1
For a fixed time t, can you tell whether the swimmer can make it? The answer only gets easier as t grows. What does a route's crossing time depend on?

## Level 2
A route can be swum at time t exactly when every cell on it has elevation at most t, so its cost is its highest cell, and the answer is the smallest such cost over all routes. Routes may go up and left as well as down and right.

## Level 3
Grow the region the swimmer can reach the way Dijkstra settles nodes, always adding next the bordering cell with the lowest elevation; the highest elevation added by the time the goal is reached is the answer.
