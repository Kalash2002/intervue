## Level 1
Searching the grid once per word works, but thousands of words that begin the same way repeat the same walks. What do the words have in common that the search could follow just once?

## Level 2
Put the words in a prefix tree and walk the grid and the tree together, one letter per step. When should a walk through the grid stop even though cells remain?

## Level 3
A trace is worth extending only while its letters are a path in the tree; the tree node it reaches also says whether a whole word ends there.
