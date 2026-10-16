# LRU Cache

Design a key-value cache that holds at most `capacity` keys. When a new key arrives and the cache is full, it evicts the key that was used least recently. Both reading a key and writing it count as using it.

Implement the class `LRUCache` in `LRUCache.java` with these members. `get` and `put` must each run in O(1) average time.

- `LRUCache(int capacity)` makes an empty cache.
- `int get(int key)` returns the value stored for `key`, or `-1` if `key` is not in the cache. A hit makes `key` the most recently used; a miss changes nothing.
- `void put(int key, int value)` stores `value` for `key` and makes `key` the most recently used. If `key` is new and the cache already holds `capacity` keys, first evict the least recently used key. Updating a key that is already present never evicts anything.

In the examples, the input is the sequence of calls on a new cache, and the output lists the values returned by the `get` calls, in order.

## Example 1

**Input:** `["LRUCache(2)", "put(5, 50)", "put(8, 80)", "get(8)", "get(5)", "put(3, 30)", "get(8)"]`
**Output:** `[80, 50, -1]`
**Explanation:** Reading 8 and then 5 leaves 8 as the least recently used key, so storing 3 evicts 8.

## Example 2

**Input:** `["LRUCache(2)", "put(1, 10)", "put(2, 20)", "put(1, 15)", "put(4, 40)", "get(1)", "get(2)"]`
**Output:** `[15, -1]`
**Explanation:** Updating 1 to 15 evicts nothing but makes 1 the most recently used, so storing 4 evicts 2.

## Example 3

**Input:** `["LRUCache(1)", "put(7, 70)", "get(7)", "put(9, 90)", "get(7)", "get(9)"]`
**Output:** `[70, -1, 90]`
**Explanation:** With room for one key, every new key evicts the previous one, even right after it was read.

## Constraints

- `1 <= capacity <= 2 * 10^5`
- `0 <= key <= 10^6`
- `0 <= value <= 10^9`
- `1 <= number of get and put calls on one cache <= 6 * 10^5`

## Follow-up

Can you make both operations O(1) without the JDK's `LinkedHashMap` doing the ordering for you?
