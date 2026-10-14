## Phase 2: peak and first breach

Implement two queries in `ExposureCalculator`:

```java
Peak peak(String account)
OptionalInt firstBreach(String account, long limitMinor)
```

`peak` rules:

- Return the highest total held on the account at any minute, and the **earliest** minute at which that total is reached.
- A hold that ends at minute 10 and a hold that starts at minute 10 do not overlap.
- Released holds and other accounts do not count.
- An account with no holds returns `Peak.NONE`.

`firstBreach` rules:

- Return the first minute at which the total held is **greater than** the limit. Equal to the limit is not a breach.
- Return `OptionalInt.empty()` if that never happens.
- Throw `IllegalArgumentException` for a negative limit.

`peakByAccount` already exists and calls `peak` for every account. Run **Phase 2 tests**.
