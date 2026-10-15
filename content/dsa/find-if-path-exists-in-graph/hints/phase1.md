## Level 1
Start at `from` and ask which stations you can reach in one ride, then which in two. Could you keep going until nothing new appears?

## Level 2
To ask "which stations touch this one" quickly, you want the connectors grouped by station. Remember each connector works in both directions, and mark stations you have already visited so you never go round a loop twice.

## Level 3
Build a list of neighbours per station, adding each connector to both ends. Then run a breadth-first or depth-first search from `from` with a visited mark, and answer `true` the moment you reach `to`. Joining the two ends of every connector in a union-find structure and comparing the roots of `from` and `to` is an equivalent route.
