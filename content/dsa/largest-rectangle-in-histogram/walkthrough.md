# How a strong round goes: Largest Rectangle in Histogram

## Approach

The best rectangle has some bar as its shortest, so it is enough to compute, for every bar, the widest rectangle in which that bar is the shortest: it stretches left and right until it meets a shorter bar. Searching outwards from every bar is quadratic on a flat skyline. Instead I walk left to right with a stack of positions whose heights never fall from bottom to top. When a bar arrives that is shorter than the one on top, the top bar can stretch no further to the right: the arriving bar is its right boundary. Its left boundary is the bar now below it on the stack, because everything between them was at least as tall. So I pop it and compute its area as its height times the width between those two boundaries, and I keep popping while the arriving bar is shorter. Bars of equal height can stay on the stack, since the later of them is popped with the earlier one as its boundary, which is correct: the earlier one has the same height and will produce the same, wider, rectangle. If the stack becomes empty, the popped bar stretches all the way to the start. After the last bar, bars still on the stack have no shorter bar to their right, so I process them as if a bar of height 0 arrived at the end; the simplest way is to run the loop one step past the end with a height of 0. That final step is needed: in a rising skyline like `[1, 2, 3, 4, 5]`, the answer 9 is only found there.

```java
static int biggestBlock(int[] heights) {
    int n = heights.length;
    int[] stack = new int[n + 1]; // positions of bars with non-decreasing heights
    int size = 0;
    int best = 0;
    for (int i = 0; i <= n; i++) {
        int current = i == n ? 0 : heights[i]; // a bar of height 0 at the end flushes the stack
        while (size > 0 && heights[stack[size - 1]] > current) {
            int height = heights[stack[--size]];
            int leftBoundary = size == 0 ? -1 : stack[size - 1]; // the bar below on the stack, or the start
            best = Math.max(best, height * (i - leftBoundary - 1));
        }
        stack[size++] = i;
    }
    return best;
}
```

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| For every bar, search outwards for shorter bars | O(n squared) | O(1) |
| Stack of rising bars, one pass | O(n) | O(n) |

## Where it shows up

Largest empty or filled region problems use this directly: finding the largest block of free space in a layout, the biggest run of consecutive time slots that stay above a load level in a capacity chart, or the largest uniform region in a simple image mask. Each bar's widest extent between nearer lower neighbours is the key quantity.

## Follow-up answers

**Why does each bar get pushed and popped at most once?** A bar is pushed exactly once when it is reached, and once popped it never returns to the stack. Popping is the only way the inner loop runs, so the total work over the whole pass is at most twice the number of bars.

**How would you find the largest rectangle of ones in a grid of zeros and ones using this?** Build a histogram for each row, where a column's height is the number of consecutive ones ending at that row, and run the stack routine on each row's histogram. The answer is the best over all rows.

**How would you do it with two arrays and no stack, and what do they hold?** Compute for each bar the position of the nearest shorter bar to its left and to its right, with two passes that jump through already-known boundaries. Then the area for bar `i` is its height times the gap between the two boundaries, and the answer is the maximum.
