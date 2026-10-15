## Level 1
Time only moves forward, so a ping that is too old now will still be too old at every later call. What does that let you do with it?

## Level 2
You can forget it for good. Pings are recorded in time order, so the oldest ones sit together at one end of whatever you store them in.

## Level 3
Keep the ping times in a queue. On each call, remove from the front every time that is no longer later than `second - window`; the queue then holds exactly the recent pings, and its size is the answer.
