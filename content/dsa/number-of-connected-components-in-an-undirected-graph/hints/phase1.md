## Level 1
Pick any machine and find everything it can reach. That whole set is one network. What do you do next, and when are you finished?

## Level 2
Every machine you have not reached yet starts a new network. Group the cables by machine first so that "who is joined to this machine" is a quick lookup, and mark machines as reached so each is handled once. Watch out for loops and repeated cables: they join nothing new.

## Level 3
Start with every machine as its own network, so the count is `n`, and think about exactly when a cable makes that count go down by one.
