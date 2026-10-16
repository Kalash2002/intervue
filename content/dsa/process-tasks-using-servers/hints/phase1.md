## Level 1
Two questions repeat for every job: which machines are free right now, and which of those is the best one. If none is free, which machine will be the first to free up?

## Level 2
You never need to look at a busy machine until it becomes free. So the machines split into two groups with different orderings: the free ones, ordered by weight then number, and the busy ones, ordered by the second they become free. What structure keeps a changing set ordered?

## Level 3
When no machine is free there is nothing to decide until one finishes, so the clock can jump straight to that moment instead of ticking second by second. Also watch the size of the numbers: finishing seconds can pass the range of an int.
