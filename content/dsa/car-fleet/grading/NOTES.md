# Grader notes: Car Fleet

Tests cover: the sample (2), an empty road, a single car, cars that never catch up (3), cars that all merge (including a tie at the gate, `[4, 6]` with speeds `[3, 2]` is one group; a strict-versus-non-strict mistake on equal times fails here), a chain of merges in two waves (hand-computed as 2), the same cars given in a different input order (processing in input order gives 3 instead of 2), and two large inputs of 100,000 cars (start positions in a scrambled order with equal speeds, where nobody catches up, and cars all timed to arrive together).

The naive solution checks, for every car, every car ahead for one that arrives no earlier, comparing times exactly with cross-multiplied `long` values; on the first large input that is about 10 billion steps, which times out at the limit and at 3x. The reference sorts once and makes one pass.
