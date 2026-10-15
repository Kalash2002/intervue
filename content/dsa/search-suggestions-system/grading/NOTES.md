# Grader notes: Search Suggestions System

Tests cover: the three samples, nothing matching the first letter, typed text longer than every product, fewer matches than the limit with a product equal to the prefix coming first, unsorted input with a limit of 2, a single product, products that are prefixes of each other, dictionary order decided letter by letter (`moat`, `mobile`, `monitor`, `mop`), and a large case of 50,000 products that share a random 600-letter start and differ in a five-digit number, with a typed word made of the 600 letters followed by `00123`. The answers are known by construction: the three smallest numbers for every prefix up to the shared start and for `0` and `00`, then `00100` to `00102`, `00120` to `00122` and `00123`.

The statement takes the number of suggestions as `limit` (1 to 10) where the best-known version fixes three; names are distinct.

The naive solution filters the whole catalogue and sorts the matches again for every letter. Measured with a cold JVM on the large test: reference 186 ms to 335 ms over three runs, naive 43.0 s, limit 3,000 ms (reference at least 9x under the limit, naive 4.8x past the 3x line of 9,000 ms). The reference sorts once and binary searches for the start of each prefix's block.
