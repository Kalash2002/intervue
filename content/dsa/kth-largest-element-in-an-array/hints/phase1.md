## Level 1
Sorting answers the question, but it puts every value in its place when you only need one value in its place. What is the least you need to know about the other values?

## Level 2
Pick one value and split the array into values smaller than it and values larger than it. You now know exactly where that value would sit in sorted order. Which side can you ignore from now on?

## Level 3
The split only helps if both sides shrink: think about what happens when many values equal the one you split on, or when you always split on the same position of a sorted array.
