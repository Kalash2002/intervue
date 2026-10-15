## Level 1

Start from the normalized names, not the display names. Write down, for one merchant, every typed text that should match it.

## Level 2

A merchant matches when the normalized typed text is the start of the normalized name or the start of the name from any later word on. Filter the directory with that rule, order by transaction count and then by normalized name, and cut to the limit.

## Level 3

1. Validate the limit first, then normalize the typed text and return nothing if it is empty.
2. For each merchant, test the start of the whole normalized name, and the start after every space.
3. Let typed text with spaces match across word boundaries; add each merchant at most once even when a word repeats.
4. Sort by count from highest, then by normalized name, and return at most `limit` results.
5. Read the directory on every call so added or replaced merchants show up.
