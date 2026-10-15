# How a strong round goes: Search Suggestions System

## Approach

There are two natural structures here and I would say both aloud before choosing. A trie holds every name letter by letter, so walking down it with the typed letters lands on the prefix, and the subtree below holds every match. A sorted array does the same job with less code: in dictionary order, the names with a given prefix are one unbroken block, so the first `limit` matches are the first `limit` names of that block.

I go with the sorted array. I sort a copy once. For each prefix of the typed text I binary search for the first name that is not smaller than the prefix, and from there I take names while they still start with the prefix, at most `limit` of them. The block of a longer prefix lies inside the block of the shorter one, so each search can start where the previous block started instead of at zero.

```java
static List<List<String>> suggest(String[] products, String typed, int limit) {
    String[] sorted = products.clone();
    Arrays.sort(sorted);
    List<List<String>> answer = new ArrayList<>(typed.length());
    int start = 0; // a longer prefix never begins before the shorter one's block
    for (int length = 1; length <= typed.length(); length++) {
        String prefix = typed.substring(0, length);
        start = firstAtLeast(sorted, prefix, start);
        List<String> found = new ArrayList<>(limit);
        for (int i = start; i < sorted.length && found.size() < limit && sorted[i].startsWith(prefix); i++) {
            found.add(sorted[i]);
        }
        answer.add(found);
    }
    return answer;
}
```

`firstAtLeast` is a lower-bound binary search from a given starting index: the same loop as for the insertion position, with `compareTo(prefix) < 0` as the test. A name equal to the prefix is not smaller, so it is found first, which is why it comes before its longer relatives.

This version differs from the best-known one in taking `limit` as a parameter instead of fixing it at three. The trie variant stores, at every node, the first `limit` names that pass through it, which makes each answer a lookup but costs memory for every node.

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Sort once, binary search per prefix | O(n log n) to sort, then O(log n + limit) per prefix | O(n) for the copy |
| Trie with the best names stored at each node | O(total letters times limit) to build, O(1) per prefix | O(total letters times limit) |
| Filter and sort the whole catalogue for every prefix | O(n log n) per prefix | O(n) |

Here n is the number of products. Times count comparisons of two names; a comparison costs up to the length of the common prefix.

## Where it shows up

`merchant-suggest` is this question with a ranking rule on top: its phase 3 asks for suggestions as the user types, and the reference `Suggester` sorts entries (one per word of a normalised merchant name, holding the rest of the name from that word on), finds the contiguous range of entries that start with the typed text by binary search, and keeps the best few of that range, busier merchants first and ties by name. It does not use a trie, and it remembers answers per typed text. The dictionary order of this question is replaced there by a transaction count, which is why it scans the range instead of reading its first entries.

## Follow-up answers

**What if the best suggestions are ranked by popularity instead of dictionary order?** The block is still contiguous, but the best of it are no longer its first. Scan the block and keep the best `limit` in a small sorted array or heap, as `merchant-suggest` does, or store the top names at each trie node when answers must be instant.

**What if the user can also delete a letter, so the typed text goes back one step?** With the array version each prefix is independent, so go back to the previous prefix's block. Keep the start indexes in a stack, one per typed letter, and pop on a delete. With a trie, keep the path of nodes and pop one.

**How would you serve this for a catalogue of millions of words that changes during the day?** Keep the sorted array or trie in memory for reads and put new words in a small side list that is merged in at each answer, then rebuild the main structure in the background. Cache the answers of short prefixes, which are asked most often.
