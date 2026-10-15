# Grader notes: Design Hit Counter

Tests cover: the sample, a counter with no pings, a window of 1, several pings in one second, the exact window boundary (a ping exactly `window` seconds old is out), a first ping at second 0, queries that repeat at the same second, and a large case of 100,000 pings one second apart with a window of 1,000, asking `recent` after every ping; the sum of the answers is 99,500,500.

The naive solution stores every ping in a list and scans the whole list on each `recent`. That is quadratic over the calls, so on the large test it exceeds the limit and 3x the limit. The reference drops expired times from the front of a queue.
