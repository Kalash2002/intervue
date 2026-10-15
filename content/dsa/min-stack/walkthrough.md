# How a strong round goes: Min Stack

## Approach

The minimum of a stack only changes at the top, and a pop undoes the last change, so every entry can carry the answer to "what was the smallest value up to and including me?". I keep two arrays of the same length: the values, and next to each value the minimum at the moment it was pushed. On a push the new minimum is the smaller of the value and the minimum below it, or the value alone when the stack is empty. `getMin` reads the stored minimum of the top entry, `top` reads the top value, and `pop` just lowers the size, which discards both together. Because each entry stores its own minimum, equal values are no problem: with pushes 3, 1, 1 the two entries for 1 each carry a minimum of 1, and popping one leaves the other. Storing a difference from the minimum instead would save a little space but breaks on extreme values, because the difference of two ints can overflow.

```java
class MinStack {
    private int[] values = new int[16];
    private int[] minimums = new int[16]; // minimums[i] is the smallest of values[0..i]
    private int size = 0;

    void push(int value) {
        if (size == values.length) {
            values = Arrays.copyOf(values, size * 2);
            minimums = Arrays.copyOf(minimums, size * 2);
        }
        values[size] = value;
        minimums[size] = size == 0 ? value : Math.min(value, minimums[size - 1]);
        size++;
    }

    void pop() {
        size--;
    }

    int top() {
        return values[size - 1];
    }

    int getMin() {
        return minimums[size - 1];
    }
}
```

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Ordinary stack, scan for the minimum in getMin | O(n) per getMin | O(1) |
| A minimum stored beside every value | O(1) per call | O(n) |

## Where it shows up

A running minimum that survives undo is what an editor or a monitoring tool needs when it shows the lowest value in a window of recent changes that can be rolled back, and what a parser keeps when it tracks the smallest nesting depth or lowest offset seen so far on the way down a tree. Keeping a summary beside each stack entry is the general trick.

## Follow-up answers

**How would you keep only one extra stack and push to it less often?** Push onto the minimum stack only when the new value is less than or equal to the current minimum, and pop from it only when the value being removed equals the current minimum. The less-than-or-equal matters: with a strict comparison, a duplicate of the minimum would be forgotten and the minimum would be wrong after one of them is popped.

**How would you add a getMax with the same cost?** Store a maximum next to every entry in the same way, updated with `Math.max` on each push. Both summaries are read from the top entry.

**How would you make it safe to use from several threads?** Make every method synchronized, or guard the arrays with one lock, because a push touches two arrays and the size and must not interleave with another call. A lock-free version is much harder, and a plain lock is enough unless profiling shows contention.
