# How a strong round goes: Daily Temperatures

## Approach

The slow way scans forward from every day until it finds a warmer one, which is quadratic when the temperatures fall or stay level. The faster way turns the question around: a day that has not yet met a warmer day is still waiting, and a new day answers every waiting day that is colder than it. I keep the waiting days on a stack of positions. Because a new day pops every colder day before it is pushed, the temperatures on the stack never rise from bottom to top, so the colder days are always on top and the ones to answer are exactly those I pop first. For each popped day the answer is the distance between today and its position. A day that is only equal to the top does not answer it, since the statement asks for strictly warmer, so the loop pops only while the top is strictly colder. Days left on the stack at the end never met a warmer day and keep their default of zero. Each day is pushed once and popped at most once.

```java
static int[] daysToWarmer(int[] temps) {
    int[] answer = new int[temps.length]; // zero means no warmer day follows
    int[] waiting = new int[temps.length]; // positions of days still waiting
    int size = 0;
    for (int day = 0; day < temps.length; day++) {
        while (size > 0 && temps[waiting[size - 1]] < temps[day]) { // strictly colder
            int earlier = waiting[--size];
            answer[earlier] = day - earlier;
        }
        waiting[size++] = day;
    }
    return answer;
}
```

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| From every day, scan forward for a warmer day | O(n squared) | O(1) besides the answer |
| Stack of days still waiting | O(n) | O(n) |

## Where it shows up

Matching each event with the next one that exceeds it is the "next greater element" pattern. It appears in monitoring, where each reading is paired with the next reading above a threshold of its own value, and in price tools that show how many days a stock stayed below each day's close before it passed it.

## Follow-up answers

**How would you solve it without a stack by walking from the right and jumping through earlier answers?** Go from the last day to the first. For day `i`, start at `i + 1` and, while that day is not warmer, jump ahead by its stored answer; a stored zero means nothing warmer follows and you stop with zero. This is still linear because the jumps skip over days already known to be no warmer.

**How would you return the warmer temperature itself instead of the number of days?** When you pop a day, store `temps[day]` in the answer for that popped position instead of the distance. Days left on the stack get a marker such as `-1`.

**How would you handle a circular week, where the days after the last one wrap around to the first?** Walk the array twice, using index `i % n` and pushing positions only during the first pass. The second pass lets days near the end find a warmer day at the start. Each day still gets pushed once, so the cost stays linear.
