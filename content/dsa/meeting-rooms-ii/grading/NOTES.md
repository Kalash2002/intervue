# Grader notes: Meeting Rooms II

Tests cover: the sample, an empty list, a single booking, back-to-back bookings that share one room, identical bookings, one long booking with short ones inside it, a staggered chain, bookings given out of order, and a large case of 60,000 shuffled bookings of length 1,000 starting one minute apart, where exactly 1,000 are active together.

The naive solution counts, for each booking, how many bookings are active at its start, comparing it with every other booking. That is quadratic, so on the large test it exceeds the limit and 3x the limit. The reference sorts the starts and ends and sweeps once.
