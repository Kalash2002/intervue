## Level 1
Each key has its own history, so start with one structure per key. Within a key, the records are added in time order. What does that order let you do when you look up a time?

## Level 2
You want the last record whose timestamp is at most the query. If the timestamps of a key sit in a sorted list, you do not have to look at all of them.

## Level 3
A history that only ever grows at the end stays sorted by time, so "the last record at or before t" can be found by halving the range, remembering the best index seen so far.
