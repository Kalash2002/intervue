# How a strong round goes: Courier Load Balancer

**Read this first.** This is one good way through the problem, not the only one. The prompts are written for interview mode, where the assistant does what you specify and nothing more.

## Minutes 0 to 6: read before you run

1. `Parcel` and `Plan`: the plan keeps couriers in list order, their parcels, their loads and the left-overs.
2. `LoadBalancer.heaviestFirst`: weight descending, then id. That matches the brief.
3. `LoadBalancer.balance`: for each parcel, a loop over every courier keeps the lightest. Read the comparison.
4. `data/parcels.csv`: six parcels, with two of 3 kg, so ties will matter.

## Phase 1: the last courier wins ties (minutes 6 to 12)

Run phase 1. The single-courier and empty tests pass; the tie test fails with C getting p1. Say: "The loads are right, only the labels are wrong, so the choice among equal loads is broken. `<=` lets a later courier replace an equal one." Change to `<` and rerun. Explain why the total loads did not change: equal couriers are interchangeable, so the plan is the same plan with names swapped.

## Phase 2: van capacity (minutes 12 to 30)

State the rule: "Same order, same lightest courier. If it does not fit there, it is left over; I do not shop around." Walk the first test by hand before coding: 8 to A, 7 to B, 5 does not fit on B (12), left over; 3 fits on B (10).

```text
Refactor LoadBalancer so balance and balanceWithCapacity share one private method that
takes a capacity (Long.MAX_VALUE for balance). In it, after choosing the lightest courier,
leave the parcel over when load + weight > capacity. Throw IllegalArgumentException for a
capacity <= 0. Keep the strict < tie-break.
```

Check that `balance` still produces exactly the phase 1 plans.

## Phase 3: festival week (minutes 30 to 45)

Run phase 3: both tests time out. "Two hundred thousand parcels times one hundred thousand couriers is twenty billion comparisons." Switch to a `PriorityQueue` of `{load, index}` ordered by load then index. Peek, check capacity, then poll, add, offer.

## Complexity

O(n log n) for the sort plus O(n log m) for the heap, against O(n * m) before.

## Follow-up answers

- Heaviest-first greedy can miss the best plan: weights 3, 3, 2, 2, 2 on two couriers give 7 and 5, while 6 and 6 is possible; the classic bound is within 4/3 of the best.
- With different van sizes the lightest courier may not have the most room; choose the courier with the most remaining room instead, which is again a heap, keyed by room.
