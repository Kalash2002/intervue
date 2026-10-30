# Grader notes: Minimum Remove to Make Valid Parentheses

Tests cover: the three samples, an empty text and one with no brackets, an already balanced text, only openers and only closers, a closer before an opener, the pairing rule that removes the earlier opener (`(a(b)` gives `a(b)`, not `(ab)`), an unpaired closer after a pair, letters kept in order around removed brackets, and a large case of 150,000 nested openers closed by 150,000 closers with one stray `)` in front and one stray `(` behind (the expected text is the nested part, by construction).

The statement fixes which brackets go (each `)` pairs with the nearest earlier unpaired `(`), because the best-known version accepts any answer with the fewest removals and a test cannot check "any". The expected values were worked out by hand from that rule.

The naive solution looks back from every `)` for the nearest unpaired `(`, skipping paired brackets; on nested input that is quadratic. Measured with a cold JVM on the large test: reference 28 ms, naive 34.3 s, limit 2,000 ms (reference 71x under the limit, naive 5.7x past the 3x line of 6,000 ms). The reference keeps a stack of positions.
