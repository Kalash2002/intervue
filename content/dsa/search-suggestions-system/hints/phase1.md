## Level 1
All the names that start with some text share that text as a prefix. If the names were in dictionary order, where would those names sit relative to each other?

## Level 2
In a sorted list, the names starting with a given prefix form one unbroken block, and the answer is the first few of that block. So the work is to find where the block begins. A longer prefix gives a block inside the shorter prefix's block.

## Level 3
Sort the names once, and for every prefix find where its block starts instead of filtering the whole list again. A search that halves the range each time finds that start quickly.
