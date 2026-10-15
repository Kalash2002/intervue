# Grader notes: Cheapest Flights Within K Stops

Tests cover: the sample at three stop limits, a direct flight against a cheaper trip with a stop, a cycle in the network, an unreachable destination, a case where cheapest-first search (ignoring the stop limit) gives a wrong answer, a case where updating prices within the same round lets a trip use more flights than allowed, and a large layered network of 14 layers of 4 cities in which every trip uses exactly 15 flights (one test with enough stops, one with one stop too few).

The naive solution tries every trip by depth-first search up to the stop limit, which is exponential in the limit: the layered network has 4 to the power 14 trips, so it exceeds the limit and 3x the limit. The reference improves the best prices one flight per round.
