## Level 1
Comparing every booking with every other one works, but it is slow for a long list, and comparing only neighbours in the given order misses clashes (Example 1). What order would make neighbours the only bookings worth comparing?

## Level 2
Put the bookings in order of start. Which earlier booking is the only one a booking needs to be compared with?

## Level 3
If a booking clashes with some earlier one in order of start, then somewhere between the two, a booking clashes with the one just before it.
