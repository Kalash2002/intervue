# How a strong round goes: Implement Trie (Prefix Tree)

## Approach

I would make the shared beginnings the structure. Each node stands for the text spelled on the path from the root, each edge is one letter, and a flag on a node says that a word ends there. The flag is the one decision that matters: without it I cannot tell the prefix "tra" from the word "tram", so `search` would say yes to every prefix. Inserting walks the letters and creates missing children; `search` walks and needs the flag; `startsWith` walks and needs only that the path exists. All three cost time in the length of the text, however many words are stored. With a lowercase alphabet, an array of 26 children is the simplest and fastest choice; a hash map per node saves memory for wide alphabets.

```java
private static final class Node {
    final Node[] children = new Node[26]; // one slot per letter
    boolean endsWord; // true when a word finishes at this node
}

void insert(String word) {
    Node node = root;
    for (int i = 0; i < word.length(); i++) {
        int slot = word.charAt(i) - 'a';
        if (node.children[slot] == null) {
            node.children[slot] = new Node(); // create the path as we go
        }
        node = node.children[slot];
    }
    node.endsWord = true;
}

boolean search(String word) {
    Node node = walk(word);
    return node != null && node.endsWord; // reaching the node is not enough
}

boolean startsWith(String prefix) {
    return walk(prefix) != null;
}
```

## Complexity

With n stored words, a query of length L, and a total of T letters stored:

| Approach | `insert` | `search` / `startsWith` | Extra space |
| --- | --- | --- | --- |
| List of words, scanned per query | O(n times L) for the duplicate check | O(n times L) | O(T) |
| Hash set of words | O(L) | `search` O(L), `startsWith` O(n times L) | O(T) |
| Trie | O(L) | O(L) | O(T) nodes at worst, times the 26 slots |

## Where it shows up

`merchant-suggest` shows suggestions as the user types, so every keystroke is a prefix query over all merchant names. Its reference answers it with sorted data and a binary search, and the walkthrough names the trie as the alternative: walk the typed letters, and the node you land on is the start of all matches. The trie pays memory for time that does not depend on the number of names.

## Follow-up answers

**How would you return every word that starts with a prefix?** Walk to the node for the prefix, then traverse its subtree depth first, building the word as you go and emitting it whenever a node has the end-of-word flag. Visiting children from `a` to `z` gives the words in alphabetical order. The cost is the size of the subtree.

**How can you cut the memory use of a trie with many long, rarely shared words?** Merge chains of single-child nodes into one edge that holds a string, which is a compressed trie, or radix tree. Use a map instead of an array of 26 children when most nodes have few children.

**How would you support deleting a word?** Walk to the word and clear its flag. Then walk back up, removing each node that has no children and no flag, and stop at the first node that is still needed by another word.
