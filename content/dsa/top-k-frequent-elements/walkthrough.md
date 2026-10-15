# How a strong round goes: Top K Frequent Elements

## Approach

There are two jobs, and I would keep them apart. First, count how often each value occurs: a hash map from value to count does it in one pass. Second, pick the k largest counts. Sorting the distinct values by count is simple and costs O(d log d) for d distinct values. A faster route uses the fact that a count can never exceed the array length: put each value into a bucket indexed by its count, then read the buckets from the highest count down until k values are collected. That is linear. A min-heap of size k is a good middle way when k is small and the data is a stream.

```java
static int[] topK(int[] nums, int k) {
    Map<Integer, Integer> counts = new HashMap<>();
    for (int value : nums) {
        counts.merge(value, 1, Integer::sum);
    }
    // buckets.get(c) holds the values that occur exactly c times; a count is at most nums.length
    List<List<Integer>> buckets = new ArrayList<>();
    for (int c = 0; c <= nums.length; c++) {
        buckets.add(null);
    }
    for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
        int c = entry.getValue();
        if (buckets.get(c) == null) {
            buckets.set(c, new ArrayList<>());
        }
        buckets.get(c).add(entry.getKey());
    }
    int[] result = new int[k];
    int filled = 0;
    for (int c = nums.length; c >= 1 && filled < k; c--) { // highest counts first
        if (buckets.get(c) == null) {
            continue;
        }
        for (int value : buckets.get(c)) {
            if (filled < k) {
                result[filled++] = value;
            }
        }
    }
    return result;
}
```

## Complexity

With n elements and d distinct values:

| Approach | Time | Extra space |
| --- | --- | --- |
| Count each value with a pass over the array, pick the largest k times | O(n times d) | O(d) |
| Hash map, then sort the distinct values by count | O(n + d log d) | O(d) |
| Hash map, then a min-heap of size k | O(n + d log k) | O(d + k) |
| Hash map, then buckets by count | O(n) | O(n) |

## Where it shows up

`duplicate-order-detector` asks for the most duplicated products, `mostDuplicatedSkus`: count how many times each SKU is duplicated, then rank. That is this question with a key that comes from an order instead of a plain number. The same count-then-rank step is behind "top searches", "busiest endpoints" and "most reported errors".

## Follow-up answers

**Can you do it in O(n) rather than O(n log n)?** Yes, with buckets indexed by count, as in the reference: counts range from 1 to n, so an array of n plus one slots holds every value, and one read from the top finds the k best. Quickselect on the counts is another linear-on-average option.

**What if the values arrive as a stream and you may ask for the top k at any time?** Keep the count map up to date on each arrival. For the answer, keep a min-heap of the k best counts, or a sorted structure keyed by count; exact answers need care because a count only goes up, so a value outside the heap can climb into it. For huge streams, sketches such as Count-Min with a heavy-hitter list give approximate answers in small memory.

**What if two values tie for the last place?** The task must say which to pick. Common rules are the smaller value, the earlier first occurrence, or return more than k values. With buckets, a tie is just two values in one bucket, so you can apply the rule while reading that bucket.
