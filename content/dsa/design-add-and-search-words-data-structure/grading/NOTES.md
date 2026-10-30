# Grader notes: Design Add and Search Words Data Structure

Tests cover: the sample, a dot standing for exactly one letter (`ox` against `.`, `ox.` and `.x`; a version that lets a dot match nothing answers `ox.` wrongly), an empty dictionary, plain patterns that must match a whole word (`kett` against `kettle`), a dot whose first fitting letter is a dead end (`abx` and `cby` with `.by`: a version that follows only the first child under a dot answers false), words that are prefixes of each other (`van`, `vane`), a word added after a failed search and added twice, twenty-letter words, and a large run.

The large test stores 50,000 seeded words of 6 to 10 letters from `a` to `m` and runs 50,000 one-dot searches whose answers are known by construction: a stored word with one letter hidden (true), a stored word with one letter hidden and another changed to a letter from `n` to `z`, which no word holds (false), and an eleven-letter pattern, longer than every word (false). Two-dot patterns are graded by the small tests.

The limit is 1,500 ms (3x is 4,500 ms). The reference, a trie that branches over every child at a dot, took 107 to 125 ms in the test on a cold JVM. The naive solution keeps a list and compares the pattern with every stored word: 15,545 ms in the test with the limit raised twentyfold, so it times out at the limit and at 3x.
