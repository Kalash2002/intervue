## Level 1
Compare two neighbouring words in the list. What, exactly, do they tell you about the alphabet, and what do they not tell you?

## Level 2
Only the first position where two neighbours differ says anything: the letter in the earlier word comes first. Letters after that position say nothing. If there is no differing position, the earlier word must not be the longer one. Collect those rules as edges between letters.

## Level 3
Every letter must come after all the letters it has rules from, which is a topological order of a graph on at most 26 letters; a cycle means no order exists.
