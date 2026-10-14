## Phase 2: suspected duplicates and the worst SKUs

A repeat a week later is a customer buying again. A repeat a few minutes later is probably a double submission. Implement two reports in `DuplicateDetector`:

```java
List<String> suspectedDuplicates(int windowMinutes)
List<SkuCount> mostDuplicatedSkus(int k)
```

`suspectedDuplicates` rules:

- Return the ids of orders that repeat the **most recent earlier** order of the same basket placed **less than** `windowMinutes` before it. Exactly `windowMinutes` apart is not suspected.
- Keep the order of the log.
- Throw `IllegalArgumentException` when `windowMinutes` is not positive.

`mostDuplicatedSkus` rules:

- Count, for every SKU, how many orders are repeats in the sense of `exactRepeats` (any earlier copy, however long ago).
- Return the `k` SKUs with the highest counts, highest first; break ties by SKU, alphabetically. Leave out SKUs with no repeats.
- Return fewer than `k` when fewer SKUs have repeats. Throw `IllegalArgumentException` for a negative `k`.

Run **Phase 2 tests**.
