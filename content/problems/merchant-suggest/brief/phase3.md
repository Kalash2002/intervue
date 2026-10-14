## Phase 3: every keystroke

**Phase 3 tests** run three workloads, each with a time limit:

1. The nightly feed: 800,000 merchants added one at a time, in the order the partner exports them.
2. 20,000 searches of four to seven letters on a directory of 100,000 merchants.
3. 30,000 customers typing a word letter by letter, with a suggestion request after every letter.

Read the tests and `Datasets` first. Each workload is slow for a different reason, and one of the reasons is in code you did not write. Say what each one stresses before you change anything. You may change any class except the tests and `Datasets`; the last rule of phase 2 still applies.
