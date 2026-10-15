# Grader notes: Minimum Window Substring

Tests cover: the sample (`"cab"`), repeated needed letters (`"cabbab"` with `"aab"` returns `"abba"`; a check on presence only returns `"ab"`), no cover (a letter missing from the text, and a letter present too few times), a need longer than the text, an empty text, the whole text as the only answer, a one-character answer, the leftmost of two equal-length answers (`"abxba"` with `"ab"` returns `"ab"`; using a non-strict comparison returns the later `"ba"`), uppercase and lowercase kept apart, and two large inputs of 100,000 characters: a need of 50,000 copies of one letter against a text of that letter (the answer is the first 50,000 characters), and a need whose letter is absent from the text (empty answer).

The naive solution starts from every position and extends until the need is covered; on the large inputs that is billions of steps, which times out at the limit and at 3x. The reference moves two edges once across the text.
