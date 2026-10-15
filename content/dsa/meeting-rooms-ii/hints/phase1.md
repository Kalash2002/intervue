## Level 1
Forget which booking goes in which room. At any single moment, how many rooms are in use, and what does that say about the number of rooms you must own?

## Level 2
The answer is the largest number of bookings active at the same moment. That number only changes at a start or at an end. How could you track it without looking at every minute?

## Level 3
Sort the start times and the end times separately. Walk through the starts in order; before counting a booking as active, release every booking whose end is at or before this start. Track the largest count you ever see.
