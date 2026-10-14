## Phase 2: van capacity

Every van can carry at most `capacityGrams`. Implement in `LoadBalancer`:

```java
Plan balanceWithCapacity(List<Parcel> parcels, long capacityGrams)
```

Rules:

- Take the parcels in the same order as `balance`: heaviest first, then by id.
- Offer each parcel to the courier with the lightest load so far, ties to the courier listed first. All vans are the same size, so that courier has the most room left.
- If the parcel fits (the load plus the parcel is **at most** the capacity), that courier takes it. Otherwise it is **left over** for tomorrow; no other courier is tried.
- The plan lists left-over parcel ids in the order they were offered.
- Throw `IllegalArgumentException` when the capacity is not positive.

Run **Phase 2 tests**.
