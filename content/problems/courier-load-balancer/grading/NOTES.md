# Grader notes: Courier Load Balancer

**Phase 1 bug.** The search for the lightest courier uses `load[i] <= load[best]`, so the last of several equally light couriers wins. The set of loads is the same as with the correct tie-break (the plan is a relabelling), which is why "the totals looked fine". Fix: strict `<`. Candidates who change the sort instead have misread the symptom.

**Phase 2.** Same order, same choice; only the "fits" check is new (`load + weight <= capacity`), and a parcel that does not fit is left over without trying other couriers. Because every van has the same capacity, the lightest courier has the most room, so nobody else could take it either; strong candidates say this out loud. Left-over order is the offer order (heaviest first).

**Phase 3.** 200,000 parcels and 100,000 couriers: the linear search is 2 x 10^10 steps and times out; a `PriorityQueue` keyed by (load, index) is O(n log m). Keeping the index in the comparator is what preserves the phase 1 tie-break; a heap keyed only by load passes phase 3 only if the checksum happens to match, and fails phase 1's tie test.

**Common assistant mistakes to watch for.** A heap comparator on load alone; polling the courier before checking capacity and forgetting to put it back; trying other couriers when the lightest one is full (not the rule); sorting by weight ascending.
