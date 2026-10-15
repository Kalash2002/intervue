# How a strong round goes: Intersection of Two Arrays

## Approach

The question I keep asking is "is this value in the other list", and scanning the other list each time is what makes the obvious solution slow. A set answers that in one step, and it ignores repeats, which is also what the problem wants since each shared value is listed once.

I put the first list into a hash set. I go through the second list; for each value that is in the set I add it to the result and remove it from the set, so a value repeated in the second list is not added twice. The statement asks for ascending order, so I sort the small result at the end. The sorted-pointers alternative is below; it gives the order for free.

```java
static int[] shared(int[] mine, int[] yours) {
    Set<Integer> mineSet = new HashSet<>();
    for (int track : mine) {
        mineSet.add(track);
    }
    List<Integer> found = new ArrayList<>();
    for (int track : yours) {
        if (mineSet.remove(track)) { // true only the first time this value is met
            found.add(track);
        }
    }
    int[] result = new int[found.size()];
    for (int i = 0; i < result.length; i++) {
        result[i] = found.get(i);
    }
    Arrays.sort(result);
    return result;
}
```

With both lists sorted (sort copies if the input must stay untouched), two indexes walk forward: advance the smaller value; when the two are equal, add it unless it equals the last value added.

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Hash set of one list, check the other, sort the result | O(n + m + k log k) for k shared values | O(n) |
| Sort both lists, two pointers | O(n log n + m log m) | O(1) beyond the copies |
| For each value of one list, scan the other | O(n times m) | O(k) |

## Where it shows up

`playlist-dedupe` compares two playlists to measure how similar they are, in `similarity`. The shared part is exactly this question: the set of tracks present in both, counted once each however many times a playlist repeats a track. Everything else in the similarity is arithmetic on that count, so getting the set intersection right, repeats included, is the first thing to settle.

## Follow-up answers

**What if both lists are already sorted?** Use the two-pointer walk and skip the set: it is linear with no extra structure. Skip over repeated values as you go so that each shared value is added once.

**What if one list is tiny and the other does not fit in memory?** Put the tiny list in the set and stream the large one through it, one pass, adding a value to the result the first time it is seen in the set. Memory is the size of the tiny list.

**How would you keep the number of times a value is shared, not just that it is shared?** Count the values of one list in a map. For each value of the other list with a positive count, add it to the result and decrement the count. A value then appears as many times as the smaller of its two counts.
