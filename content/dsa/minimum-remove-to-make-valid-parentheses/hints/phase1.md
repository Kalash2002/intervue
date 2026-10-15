## Level 1
Ask which brackets can never be part of a balanced text. A `)` is hopeless when nothing is waiting to be closed as it arrives. What makes a `(` hopeless?

## Level 2
At each `)` you need to know whether an earlier `(` is still unpaired, and which one is the nearest. Remembering the waiting openers in the order they appeared is enough. Which data structure hands you the most recent one?

## Level 3
Keep positions rather than characters, so that you can mark exactly which brackets to drop. When the scan is over, build the answer from the positions you did not mark.
