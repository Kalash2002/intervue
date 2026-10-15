## Level 1
Pick a stretch of the string. How many characters in it would have to change to make all of them equal, and which letter should they become?

## Level 2
In a stretch, the letter to keep is the one that already appears most often; every other character must change. So a stretch is acceptable when its length minus its highest letter count is at most k. How could you test stretches without recounting each one from scratch?

## Level 3
Keep a window with letter counts, grow it on the right, and when it needs more than k changes, shrink it from the left. The answer is the largest window ever seen, so think about whether the highest count really has to drop when you shrink.
