## Phase 3: a full day

**Phase 3 tests** run two workloads, each with a time limit:

1. One noisy client with 500,000 requests.
2. 40,000 clients with eight requests each, audited with `rejectionCounts`.

Read the tests and `Datasets` first. The two workloads are slow for different reasons, and not both reasons are in `RateAudit`. Say what each one stresses before you change anything. You may change any class except the tests and `Datasets`.
