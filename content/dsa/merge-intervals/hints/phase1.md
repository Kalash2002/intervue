## Level 1
Take two ranges. How can you tell, from their endpoints alone, whether they belong together? Now think about what would make that check much easier to apply across the whole list.

## Level 2
If the ranges were ordered by start, a range could only join the group that is currently open, never one from earlier. What would you need to remember about the open group to decide whether the next range joins it?

## Level 3
A long range can contain several later ones whole. When a range joins the open group, the group's end must not move backwards, so think about what the new end should be.
