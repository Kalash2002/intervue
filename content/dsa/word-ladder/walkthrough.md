# How a strong round goes: Word Ladder

## Approach

I treat each word as a node and join two words when they differ in exactly one letter. Every change costs the same, so "fewest changes" is a shortest path in an unweighted graph, and breadth-first search finds it: it reaches all words one change away, then all words two changes away, and the first time it reaches `end` is along a shortest ladder. I claim a word when I queue it, not when I take it off the queue, so no word is queued twice.

The part that decides the running time is finding neighbours. Comparing a word with the whole list works but costs the list size for every word, which is quadratic overall. Instead I group the allowed words by shape, the word with one position replaced by a star. `cord` has the shapes `*ord`, `c*rd`, `co*d` and `cor*`, and two words are one change apart exactly when they share a shape. So the neighbours of a word are the words in the groups of its own shapes, found with a handful of hash lookups.

```java
static int fewestChanges(String begin, String end, List<String> allowed) {
    if (begin.equals(end)) {
        return 0;
    }
    Map<String, List<String>> byShape = new HashMap<>();
    for (String word : allowed) {
        for (int i = 0; i < word.length(); i++) {
            byShape.computeIfAbsent(shape(word, i), k -> new ArrayList<>()).add(word);
        }
    }
    Set<String> reached = new HashSet<>(); // claimed when queued
    Deque<String> queue = new ArrayDeque<>();
    Map<String, Integer> changes = new HashMap<>();
    reached.add(begin);
    changes.put(begin, 0);
    queue.add(begin);
    while (!queue.isEmpty()) {
        String word = queue.poll();
        int next = changes.get(word) + 1;
        for (int i = 0; i < word.length(); i++) {
            for (String neighbour : byShape.getOrDefault(shape(word, i), List.of())) {
                if (reached.add(neighbour)) {
                    if (neighbour.equals(end)) {
                        return next;
                    }
                    changes.put(neighbour, next);
                    queue.add(neighbour);
                }
            }
        }
    }
    return -1;
}
```

`shape(word, i)` returns the word with position `i` replaced by a star. A word that is not allowed can never be queued, so an `end` outside the list simply stays unreached and the answer is -1.

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| BFS, neighbours found by comparing with every allowed word | O(N squared x L) | O(N) |
| BFS, neighbours found through shape groups | O(N x L squared) | O(N x L squared) |
| Try all 25 replacement letters at every position, look each up in a set | O(N x L x 26 x L) | O(N x L) |

N is the number of allowed words and L the word length. Depth-first search is not on the table: it finds a ladder, not the shortest one.

## Where it shows up

`friend-suggestion` asks how many steps apart two users are in a friendship network, which is the same question with users instead of words: nodes are people, an edge is a friendship, and the breadth-first search with a claim-on-queue visited set is the reference for `degreesApart`. The shape groups here play the role of the adjacency list that the network already has.

## Follow-up answers

**How would you return the whole sequence of words, not just the number of changes?** Store, for each reached word, the word it was reached from. When `end` is reached, follow those links back to `begin` and reverse the result. The extra cost is one map entry per word.

**What if the dictionary is huge but you ask many begin and end pairs?** Build the shape groups once and reuse them for every query; only the search is repeated. If the same begin word is asked often, one search from it answers every end word at once.

**How does searching from both ends at once help?** Run a search from `begin` and one from `end`, always expanding the smaller frontier, and stop when they meet. The frontiers grow roughly by the branching factor each round, so two searches of half the depth touch far fewer words than one search of full depth.
