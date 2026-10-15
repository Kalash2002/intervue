# How a strong round goes: Sliding Window Maximum

## Approach

Neighbouring windows share all but one element, so rescanning each window wastes the work. The useful observation is about candidates: when a new element arrives, every earlier element that is not larger can never be the maximum of any window again, because the new one stays in the window longer. So I keep only the candidates, in a deque of positions whose values decrease from front to back. A new element removes the weaker candidates from the back and joins there. The front is the maximum; it is dropped from the front when its position leaves the window. Each position enters and leaves the deque at most once, so the whole pass is linear. Storing positions rather than values is what lets me tell when the front has expired.

```java
static int[] maxima(int[] nums, int k) {
    int n = nums.length;
    int[] result = new int[n - k + 1];
    int[] deque = new int[n]; // positions; values decrease from front to back
    int front = 0;
    int back = 0; // the deque holds deque[front .. back - 1]
    for (int i = 0; i < n; i++) {
        while (back > front && nums[deque[back - 1]] <= nums[i]) {
            back--; // smaller or equal values before a new one can never win again
        }
        deque[back++] = i;
        if (deque[front] <= i - k) {
            front++; // the front left the window
        }
        if (i >= k - 1) {
            result[i - k + 1] = nums[deque[front]]; // the front is the window maximum
        }
    }
    return result;
}
```

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Rescan every window | O(n times k) | O(1) beyond the result |
| Max-heap with lazy removal of expired entries | O(n log n) | O(n) |
| Deque of candidate positions | O(n) | O(k) at most (O(n) in the array form above) |
| Prefix and suffix maxima in blocks of k | O(n) | O(n) |

## Where it shows up

`rate-limiter-audit` replays a limiter whose question is always about the last window of time: requests older than the window are removed from the front of a queue, and new ones join at the back. That is the same queue, with the same rule that expired entries leave from the front. This question adds the extra trick of removing weaker entries from the back so the front always answers "maximum".

## Follow-up answers

**Why is each element pushed and popped at most once?** An index enters the deque once when it arrives, and once it leaves, from the back because a larger value came or from the front because it expired, it never returns. So the total number of pushes and pops is at most 2n, whatever the window size.

**How would you solve it with a heap, and what does that cost?** Put pairs of value and position into a max-heap. At each step, pop from the top while the top position is outside the window, then read the top. Every element is pushed once and popped at most once, so the cost is O(n log n), and the heap can hold up to n entries.

**What changes if you need the minimum, or the median, of each window?** The minimum uses the same deque with the comparison reversed. The median needs a different structure, such as two heaps with lazy deletion or a balanced tree of the window, and costs O(n log k).
