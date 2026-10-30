## Level 1
Ask which brackets can never be part of a balanced text. A `)` is hopeless when nothing is waiting to be closed as it arrives. What makes a `(` hopeless?

## Level 2
At each `)` you need to know whether an earlier `(` is still unpaired, and which one is the nearest. Remembering the waiting openers in the order they appeared is enough. Which data structure hands you the most recent one?

## Level 3
Two brackets can look the same and still only one of them must go, so a bracket to drop has to be named by where it sits, not by what it is. Decide how to record which positions go, and build the answer from the rest.
