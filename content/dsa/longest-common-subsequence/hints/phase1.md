## Level 1
Matching each letter of one word to its first copy in the other, as early as possible, can use up the letters a better match needed. Try it on Example 1 and see where it goes wrong.

## Level 2
Look at the last letters of the two strings. If they are equal, keeping them as a pair is never worse than not; if they differ, at least one of them is not in the answer. Either way, the question becomes the same question about shorter strings.

## Level 3
There are only as many different questions as there are pairs of prefixes, one prefix of each string, and each one's answer comes from the answers for pairs one letter shorter.
