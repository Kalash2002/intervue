## Level 1
Time only moves forward, so a ping that is too old now will still be too old at every later call. What does that let you do with it?

## Level 2
You can forget it for good. Pings are recorded in time order, so the oldest ones sit together at one end of whatever you store them in.

## Level 3
A ping that is exactly `window` seconds old is already out. Write the test for the oldest stored ping so that this boundary comes out right, and check it on Example 3.
