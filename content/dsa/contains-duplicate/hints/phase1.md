## Level 1
Comparing every position with every later position works, but its cost grows with the square of the length. What do you need to know about a value when you reach it?

## Level 2
You only need to know whether you have seen that exact value earlier. Which data structure answers "is this in here?" quickly?

## Level 3
Keep the values you have already walked past in a collection that answers "is this in here?" in constant time, and ask it before you add the current value.
