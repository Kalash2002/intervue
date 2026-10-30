## Level 1
Any path has one highest node, where it stops climbing and starts going down. If you knew that node, how long could the path be?

## Level 2
At a given node, the longest path that turns there goes as deep as possible on the left and as deep as possible on the right. Both of those are heights. Computing a height from scratch at every node repeats a lot of work.

## Level 3
A function that returns a node's height has already seen both children's heights, which is all the path that turns at that node needs.
