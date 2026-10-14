## Level 1

Count how many earlier orders your code inspects for the last order of a 400,000 order log. Both reports in the tests need to look at each order a small, fixed number of times.

## Level 2

Any approach that scans back through earlier orders is quadratic. A hash map from basket key to the latest minute, and a hash set of seen keys, answer the "seen before" and "how long ago" questions in constant time per order.

## Level 3

1. Make sure `OrderKey` has a working `equals(Object)` and `hashCode`, because every lookup depends on them.
2. Replace any backward scan with a single forward pass that reads and updates a map.
3. Count SKUs in a map during the same pass rather than searching a list of counts.
4. Sort only the per-SKU totals at the end; there are few SKUs, so that sort is cheap.
5. Run Phase 3 tests and compare the time each one reports with its limit.
