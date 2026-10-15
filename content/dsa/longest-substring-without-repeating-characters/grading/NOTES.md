# Grader notes: Longest Substring Without Repeating Characters

Tests cover: the sample (5), an empty string, one character, a string of one repeated character, a string of all different characters, `"xyzzyx"` (3), `"abba"` (2; an earlier copy that lies before the start must not pull the start back, which would give 3), `"dvdf"` (3; restarting from the repeat instead of sliding gives 2), case-sensitive characters, characters outside Latin-1 (a table sized for 128 or 256 characters fails), and two large inputs: 100,000 characters in which a window of 50,000 different characters repeats, and 50,000 all different characters.

The naive solution extends from every start with a set until a repeat; because the alphabet is any `char`, a stretch can be 50,000 long and the work is quadratic, which times out at the limit and at 3x. The reference uses a last-seen table.
