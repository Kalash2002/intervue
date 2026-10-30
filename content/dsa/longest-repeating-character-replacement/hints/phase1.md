## Level 1
Pick a stretch of the string. How many characters in it would have to change to make all of them equal, and which letter should they become?

## Level 2
In a stretch, the letter to keep is the one that already appears most often; every other character must change. So a stretch is acceptable when its length minus its highest letter count is at most k. How could you test stretches without recounting each one from scratch?

## Level 3
The answer is the largest acceptable window ever seen. Think about whether the window ever needs to get smaller than the best size found so far.
