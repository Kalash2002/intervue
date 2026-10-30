# Grader notes: Car Pooling

Tests cover: the sample both ways, no requests, a single group larger than the seats, a group leaving at the minute another boards (and one that leaves a minute later), requests given out of order with the peak reached twice, identical requests, a request spanning the whole time range with short ones inside it, and a large case of 60,000 shuffled requests of one rider and length 1,000 starting a minute apart, where exactly 1,000 are aboard at the peak (true with 1,000 seats, false with 999).

The naive solution adds up, for each request, the riders of every request aboard at its boarding minute. That is quadratic, so on the large test it exceeds the limit and 3x the limit. The reference sorts boarding and leaving events and sweeps once.
