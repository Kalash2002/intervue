## Phase 3: large networks

The production network has thousands of nodes and tens of thousands of corridors. **Phase 3 tests** run three workloads, each with a time limit:

1. Unrelated lookups on a dense network.
2. Lookups on a sparse network where routes may be up to 40 corridors long.
3. A settlement batch: thousands of lookups from one origin. The network does not change during the batch. It does change afterwards, and the next answer must reflect that.

Read the tests and `Datasets` before changing anything. Say which workload your current code fails and why, then decide what to change. You may change any class except the tests and `Datasets`.
