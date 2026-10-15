## Level 1
Take two ranges. How can you tell, from their endpoints alone, whether they belong together? Now think about what would make that check much easier to apply across the whole list.

## Level 2
If the ranges were ordered by start, a range could only join the group that is currently open, never one from earlier. What would you need to remember about the open group to decide whether the next range joins it?

## Level 3
Sort by start. Keep one open range. For each next range, if it starts no later than the open range ends, extend the end if it reaches further; otherwise close the open range, store it and open a new one. Do not forget to store the last open range when the list ends.
