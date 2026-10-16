## Level 1
All the names that start with some text share that text as a prefix. If the names were in dictionary order, where would those names sit relative to each other?

## Level 2
In a sorted list, the names starting with a given prefix form one unbroken block, and the answer is the first few of that block. So the work is to find where the block begins. A longer prefix gives a block inside the shorter prefix's block.

## Level 3
Finding where a block begins is the same question as finding where a value would be inserted into a sorted list: the first name that is not smaller than the prefix.
