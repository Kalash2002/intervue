# Interleaving String

A riffle shuffle cuts a deck into two piles and lets the cards fall one at a time from either pile onto a new deck, so the two piles mix but the cards of each pile keep their order. Here a card is a lowercase letter, and a pile or a deck is a string listing its cards in the order they fall.

Implement `Riffle.canShuffle(String left, String right, String deck)` in `Riffle.java` and return `true` if `deck` can come out of a riffle of the piles `left` and `right`: every card of both piles is used exactly once, and the cards that came from each pile appear in `deck` in that pile's order. Return `false` otherwise. Either pile may be empty.

## Example 1

**Input:** `left = "lime"`, `right = "leek"`, `deck = "limleeke"`
**Output:** `true`
**Explanation:** l, i and m fall from `lime`, then l, e, e and k from `leek`, then the last e from `lime`. Both piles offer the first card, an l, and the fifth, an e; taking the fifth from `lime` would leave e, k, e in the deck for the e, e, k still in `leek`.

## Example 2

**Input:** `left = "pear"`, `right = "plum"`, `deck = "plumepar"`
**Output:** `false`
**Explanation:** The deck holds exactly the cards of the two piles, and each pile can be found in it on its own, but both need the first p: `plum` because its l comes right after it, and `pear` because the deck's only e comes before the second p.

## Example 3

**Input:** `left = "fig"`, `right = "pea"`, `deck = "fpeig"`
**Output:** `false`
**Explanation:** `fpeig` is `fig` riffled with `pe`, but the a of `pea` never falls, and every card of both piles must be used.

## Constraints

- `0 <= left.length() <= 100`
- `0 <= right.length() <= 100`
- `0 <= deck.length() <= 200`
- `left`, `right` and `deck` contain only lowercase English letters.

## Follow-up

What if you also had to say which pile each card of the deck came from?
