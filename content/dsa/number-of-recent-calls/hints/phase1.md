## Level 1
After a new call arrives, which of the earlier calls can still matter, and which can never matter again? Remember that time only moves forward.

## Level 2
A call that is too old now will be too old for every later call as well. Which structure lets you look at the oldest remembered call and discard it, and add the newest on the other end?

## Level 3
Keep the arrival times in a queue. On each call, add its time at the back, then remove from the front while the front is older than the start of the range. The answer is the size of the queue.
