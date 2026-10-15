## Level 1
For a value you meet now, which earlier position of the same value is the only one worth comparing with?

## Level 2
The nearest earlier one. If the nearest earlier occurrence is too far away, every older occurrence is too. What would you store per value to find it at once?

## Level 3
Keep a map from value to the last position where you saw it. At each position, look the value up: if it is there and the distance is within the gap, you are done; either way, overwrite the stored position with the current one.
