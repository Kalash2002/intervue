## Level 1
A list without a cycle ends at `null`. A list with one never ends, so walking it until it ends is no answer. What would tell you that you are going round?

## Level 2
Remembering every node you have visited works; remember the nodes themselves, not their values, since values may repeat. Can you tell without remembering anything?

## Level 3
Two walkers at different speeds on a loop: the faster one gains one node per step on the slower one, so it cannot pass it without landing on it.
