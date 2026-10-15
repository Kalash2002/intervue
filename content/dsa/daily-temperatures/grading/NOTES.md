# Grader notes: Daily Temperatures

Tests cover: the sample (`[2, 1, 3, 2, 1, 0]`), an empty array and a single day, equal temperatures (`[5, 5, 5]` returns all zeros, and `[3, 3, 4]` returns `[2, 1, 0]`; a check with `<=` that treats equal as warmer gets these wrong), falling temperatures, rising temperatures, a warmer day far away, one hot day answering many waiting days (`[5, 4, 3, 2, 1, 9]`), negative temperatures, a check that the input array is left unchanged, and two large inputs of 200,000 days (all equal, and a long flat run followed by one warmer day).

The naive solution scans forward from every day; on both large inputs that is tens of billions of steps, which times out at the limit and at 3x. The reference uses a stack.
