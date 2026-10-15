# Grader notes: Find if Path Exists in Graph

Tests cover: the sample (reachable, separate island, the reverse direction), a single station with no connectors, an isolated start, a loop in the network with an unreachable station, connectors given in the opposite order to the ride, repeated connectors, a long detour that must be followed through several stations, and a large case of 60,000 stations on one line whose connectors are listed starting from the far end.

The naive solution keeps a set of reached stations and rescans the whole connector list until a pass adds nothing. With the connectors listed from the far end, each pass reaches just one more station, so the work is quadratic and it exceeds the limit and 3x the limit. The reference builds neighbour lists and searches once.
