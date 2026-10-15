## Level 1
Think of a stretch as a start and an end. Which stretches are worth looking at, and when does adding one more character on the right make a stretch invalid?

## Level 2
Checking every start and end is slow. When the new character on the right is already inside the stretch, you do not need to throw the whole stretch away. What is the smallest change to the start that makes the stretch valid again?

## Level 3
Keep a start position and move the end forward. When the character at the end was seen before, move the start to just after that earlier occurrence, but never move it backwards. Think about what happens when the earlier occurrence lies before the current start.
