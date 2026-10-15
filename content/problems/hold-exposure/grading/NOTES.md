# Grader notes: Hold Exposure

**Phase 1 bug.** `exposureAt` accumulates into `int total` with `total += hold.amountMinor()`. A compound assignment casts silently, so the `long` amount is narrowed to `int` with no compiler warning. Totals above about 2.1 billion paise wrap to negative. Fix: `long total`. Most tests pass because retail totals fit in an `int`; in the corporate sample test the first two assertions pass and only the third overflows.

**Phase 2.** The peak can only change where a hold starts, so evaluating the total at every start is correct but O(n squared). The intended approach is a sweep: two events per hold (start +amount, end -amount), sorted by minute, with ends before starts inside the same minute. That ordering is what makes touching holds not overlap; getting it wrong fails `holdsThatOnlyTouchDoNotOverlap` and `aHoldEndingAsAnotherStartsDoesNotBreach`. "Earliest minute" requires a strict greater-than when updating the best. The running total must be `long` again.

**Phase 3.**
- Workload 1 (one account, 120,000 holds) needs the O(n log n) sweep instead of the quadratic scan.
- Workload 2 (40,000 small accounts) is slow because of `HoldBook`, not the calculator: `holdsFor` scans every hold for every account, and `release` scans the whole list for each id. The fix is to index holds by account (and by id for release). Strong candidates find this by reading `HoldBook` and the test before touching code.
- Calling the sweep three times for one account re-sorts each time; caching is optional, not required.

**Common assistant mistakes to watch for.** Sorting starts before ends at the same minute; using `>=` so the latest minute of a tied peak is returned; an `int` running total in the new code; optimising the calculator for workload 2 and never looking at `HoldBook`; changing `accounts()` so an account disappears when its last hold is released (the tests expect 40,000 accounts).
