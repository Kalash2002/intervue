# How a strong round goes: Trapping Rain Water

## Approach

Take one position. The water above it stands as high as the lower of two things: the tallest bar to its left and the tallest bar to its right, and the amount above the position is that level minus its own height, never below zero. The slow way computes both maxima by scanning for every position, which is quadratic. The first improvement is to compute them once, with a left-to-right and a right-to-left sweep, which needs two arrays. I can do without them by working in from both ends. I keep a position and a running maximum on each side. At each step I handle the side whose running maximum is smaller. The reason it is enough: if the left maximum is the smaller one, then the water above the left position is limited by the left maximum, because I know a bar at least that high exists on the right (the right maximum is not smaller). So I can finish that position now, using only the left maximum, and move on. The mirror argument holds on the right. With a few bars of equal height, either side may go.

```java
static int trapped(int[] heights) {
    int left = 0;
    int right = heights.length - 1;
    int leftMax = 0; // tallest bar seen so far from the left
    int rightMax = 0; // tallest bar seen so far from the right
    int total = 0;
    while (left < right) {
        if (heights[left] < heights[right]) {
            // a bar at least this tall stands to the right, so the left maximum decides
            leftMax = Math.max(leftMax, heights[left]);
            total += leftMax - heights[left];
            left++;
        } else {
            rightMax = Math.max(rightMax, heights[right]);
            total += rightMax - heights[right];
            right--;
        }
    }
    return total;
}
```

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Scan left and right for the maxima of every position | O(n squared) | O(1) |
| Two arrays of prefix and suffix maxima | O(n) | O(n) |
| Two positions moving inwards | O(n) | O(1) |

## Where it shows up

Measuring what a profile holds between its peaks comes up in terrain and flood models (how much a surface can pool) and in capacity checks on a time series, such as how much a buffer would fill and drain between two peaks. The left and right running maximum is also used in other "compare with the best so far on each side" questions.

## Follow-up answers

**How would you do it with two extra arrays, and what does the two-position version save?** Fill `leftMax[i]` with the tallest bar at or before `i` and `rightMax[i]` with the tallest bar at or after `i`, then add `min(leftMax[i], rightMax[i]) - heights[i]` for every `i`. The two-position version gets the same total in constant memory.

**Can you solve it with a stack, and what does it count on each pop?** Yes. Keep a stack of bar positions with falling heights. When a taller bar arrives, pop the lowest bar as a floor, and add the water over it: the width between the new bar and the one now on top of the stack, times the lower of those two heights minus the floor. It counts water in horizontal layers.

**How would the problem change in two dimensions, with a grid of heights?** A cell's level now depends on the lowest way out in any direction, not just left and right. The usual solution starts from the border cells in a min-heap and floods inwards, always expanding from the lowest border seen so far.
