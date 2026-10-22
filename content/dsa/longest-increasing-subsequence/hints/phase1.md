## Level 1
The picked values may skip positions, so looking only at neighbours is not enough, and taking every larger value as soon as it appears can block a longer pick. Work out what the best pick ending at each position is.

## Level 2
The best pick ending at a position extends the best pick ending at some earlier, smaller value. That comparison against every earlier value is O(n²), too slow for 200,000 values. What is the least you need to remember about the picks of each length?

## Level 3
For each length, only the smallest value that a rising pick of that length can end with matters, and those smallest ends rise with the length.
