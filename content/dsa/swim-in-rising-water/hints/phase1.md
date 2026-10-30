## Level 1
For a fixed time t, can you tell whether the swimmer can make it? The answer only gets easier as t grows. What does a route's crossing time depend on?

## Level 2
A route can be swum at time t exactly when every cell on it has elevation at most t, so its cost is its highest cell, and the answer is the smallest such cost over all routes. Routes may go up and left as well as down and right.

## Level 3
Grow the reachable region one cell at a time, always through the lowest cell on its border.
