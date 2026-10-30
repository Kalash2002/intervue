## Level 1
If you write only the values, in any one traversal order, can you tell `[7,7]` from `[7,null,7]`? What is missing from the text?

## Level 2
Write a marker wherever a child is missing. With the markers in place, does a single traversal order hold enough to rebuild the tree, and in which order should the decoder read it?

## Level 3
Decode in the same order you encoded, reading the text from the front with one shared position: the markers tell each step when a side is finished, so nothing else about the shape needs storing.
