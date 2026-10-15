## Level 1
Each key has its own history, so start with one structure per key. Within a key, the records are added in time order. What does that order let you do when you look up a time?

## Level 2
You want the last record whose timestamp is at most the query. If the timestamps of a key sit in a sorted list, you do not have to look at all of them.

## Level 3
Keep a map from key to two parallel lists: timestamps and values, appended on each set (they stay sorted because timestamps increase). For a get, binary search the timestamps for the rightmost index whose timestamp is at most the query; return the value at that index, or the empty string if no index qualifies.
