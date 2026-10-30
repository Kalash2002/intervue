## Level 1
Comparing a pattern with every stored word works, but it gets slow with tens of thousands of words and as many searches. Without dots, which structure would answer a lookup by following the letters of the query alone?

## Level 2
Store the words in a tree with one edge per letter, marking the nodes where a word ends. A plain letter in the pattern picks one child. What is a dot allowed to pick?

## Level 3
At a dot, the search does not choose a child; it tries each child that exists and succeeds if any of them leads to a word end exactly when the pattern runs out.
