# How a strong round goes: Container With Most Water

## Approach

Every pair of walls is a candidate, but there are about n squared of them. I start with the widest tank, the two outer walls. Say the left wall is the shorter. Any tank that keeps the left wall and uses a wall closer in has a smaller width and a level that is still at most the left wall's height, so it cannot hold more than the tank I just measured. That means the left wall can be dropped for good: I move that position one step inwards. If the right wall is shorter, the same argument drops it. When the walls are equal, either can go. After every step I measure the new tank and keep the best volume. Each step removes one position, so the walk takes n steps and finishes with the two positions meeting. The reason to move the shorter wall and not the taller one: moving the taller one can only lower or keep the level, since the shorter wall still limits it, while the width shrinks.

```java
static int maxVolume(int[] heights) {
    int left = 0;
    int right = heights.length - 1;
    int best = 0;
    while (left < right) {
        int level = Math.min(heights[left], heights[right]);
        best = Math.max(best, level * (right - left));
        if (heights[left] < heights[right]) {
            left++; // the shorter wall can never do better with a narrower tank
        } else {
            right--;
        }
    }
    return best;
}
```

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Measure every pair of walls | O(n squared) | O(1) |
| Two positions, move the shorter wall | O(n) | O(1) |

## Where it shows up

Choosing two boundaries where the weaker side limits the outcome shows up in capacity planning and layout problems: picking two points on a line where the shorter of two limits sets the usable height, or the narrower of two links sets the throughput. The idea of starting wide and discarding the side that cannot improve is the same as in the sorted-pair search.

## Follow-up answers

**Why is it safe to move the shorter wall and never the taller one?** With the shorter wall fixed, every narrower tank has a level no higher than that wall, so none can beat the tank just measured. All tanks that include the shorter wall are therefore covered, and it can be dropped. The taller wall may still be part of a better tank with a wall that is taller than the one we dropped.

**How would you also return the two wall positions?** Keep the positions of the best tank when `best` is updated, and return those two numbers instead of the volume, or along with it.

**How does the problem change if water between the two walls may also be held by taller walls in between?** Then the walls in between matter and the question becomes how much water a whole terrain traps, which is the trapping rain water problem.
