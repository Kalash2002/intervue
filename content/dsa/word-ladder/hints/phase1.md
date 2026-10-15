## Level 1
Think of every word as a point and join two points when the words differ in exactly one letter. What are you then asking about this picture?

## Level 2
You want the shortest route in a picture where every step costs the same. Which kind of search reaches things in order of how many steps away they are? Also decide how you will find the neighbours of a word without comparing it with the whole list each time.

## Level 3
Search outward in rounds from `begin`, remembering which words you have already reached. To find neighbours quickly, group the allowed words by shape: the word with one position blanked out. Two words are neighbours exactly when they share a shape.
