# Grader notes: Time Based Key-Value Store

Tests cover: the sample history of one key, a query before the first record, an exact timestamp, a query after the last record, an unknown key, independent keys, a key with one record, a boundary at timestamp 0, and a large run of 300,000 sets on one key followed by 300,000 seeded random gets, checked against values computed from the timestamp.

The naive solution scans the history of the key from the start for every get, so a long history makes it quadratic; it times out on the large test at the limit and at 3x. The reference binary-searches the timestamps.
