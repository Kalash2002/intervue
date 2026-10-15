# Grader notes: Cheapest Flights Within K Stops

Tests cover: the sample at three stop limits, a direct flight against a cheaper trip with a stop, a cycle in the network, an unreachable destination, a case where cheapest-first search (ignoring the stop limit) gives a wrong answer, a case where updating prices within the same round lets a trip use more flights than allowed, and a large layered network of 15 layers of 4 cities in which every trip uses exactly 16 flights (one test with enough stops, one with one stop too few).

The naive solution tries every trip by depth-first search up to the stop limit, which is exponential in the limit: the layered network has 4 to the power 15 trips, so it exceeds the limit and 3x the limit. The reference improves the best prices one flight per round.

Measured with a cold JVM on the large test (both assertions, one run each): reference about 1 ms in-test, naive 23.7 s and 30.7 s over two runs, limit 1,000 ms. Reference is far under limit/8 (125 ms); naive is about 8x past the 3x line of 3,000 ms (1.5 x line = 4,500 ms). With 14 layers the naive took only 5.5 to 8.2 s, too close to the line, hence 15 layers (n = 62, maxStops = 15, within the constraints).
