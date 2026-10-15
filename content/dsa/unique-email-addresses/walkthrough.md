# How a strong round goes: Unique Email Addresses

## Approach

Two addresses are the same exactly when they reduce to the same plain form, and each address can be reduced without looking at any other. So I split the work: first a function that turns one address into its canonical text, then a hash set that counts the distinct canonical texts. Comparing every address with every other would repeat work that the set does in one step per address.

The canonical form keeps the domain as written, and cleans the name: stop at the first `+`, and skip every `.` before it. Both rules are applied while walking the name once. I break out of the loop at the `+`, so dots after it never matter. I do not touch dots in the domain, since `a.com` and `ac.om` are different places.

```java
static int countDistinct(String[] emails) {
    Set<String> mailboxes = new HashSet<>();
    for (String email : emails) {
        mailboxes.add(normalise(email));
    }
    return mailboxes.size();
}

private static String normalise(String email) {
    int at = email.indexOf('@');
    StringBuilder name = new StringBuilder(at);
    for (int i = 0; i < at; i++) {
        char c = email.charAt(i);
        if (c == '+') {
            break; // everything after the first plus is ignored
        }
        if (c != '.') {
            name.append(c);
        }
    }
    return name.append(email, at, email.length()).toString(); // the domain is kept as written
}
```

Strings are immutable, so the cleaned name is a new value that must be kept: calling `replace` and dropping the result changes nothing.

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Normalise each address, count distinct with a hash set | O(total characters) | O(total characters) |
| Normalise each address, check it against a list of those already counted | O(addresses squared times length) | O(total characters) |

## Where it shows up

`playlist-dedupe` uses the same shape for songs: `SongKey.of` normalises the title and artist (trimmed, lower case, single spaces, a trailing "(...remaster...)" removed) and joins them into one key, and `PlaylistTools.dedupe` keeps a track only when `seen.add(key)` is true, so the first occurrence stays. The same normalise-then-set idea sits behind `similarity`, which builds a set of keys per playlist. Here the rule is about dots and tags in a name; there it is about case and a remaster suffix.

## Follow-up answers

**How would you return the groups of addresses that reach the same mailbox?** Use a map from the canonical form to a list of the original addresses instead of a set. The groups are the values of the map, and its size is the count.

**What if a new rule is added, such as ignoring case in the name?** Add it to `normalise` and nothing else changes, which is the point of reducing each address on its own. Only the rule needs care: lower-casing the domain is safe, while lower-casing the name is a decision about the provider.

**What if the list is too large to keep every normalised address in memory?** Store a 64-bit hash of each canonical form in the set instead of the text, accepting a tiny chance of two mailboxes colliding, or sort the canonical forms on disk and count the distinct ones in one pass over the sorted file.
