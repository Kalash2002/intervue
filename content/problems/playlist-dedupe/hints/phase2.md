## Level 1

Both reports are about distinct things: distinct users per artist, and distinct songs per playlist. Think about which collection keeps only distinct values and answers "is this one in it" quickly.

## Level 2

For listeners, keep a map from normalised artist to a set of users and add each play's user to its artist's set; the count is the set size. For similarity, turn each playlist into a set of song keys, count the keys of one set that the other contains, and work out the union from the two sizes.

## Level 3

1. Reject a negative `k` before doing any work.
2. For each play, find the track in the catalogue; skip unknown ids; normalise the artist name.
3. Add the user to that artist's set in a map, then build one count per artist from the set sizes.
4. Sort by count descending, then name ascending, and keep the first `k`.
5. For similarity, build both sets of song keys, count shared keys, compute `all` as the sum of the sizes minus shared, return 0 when `all` is 0, and do the percentage with integer division.
