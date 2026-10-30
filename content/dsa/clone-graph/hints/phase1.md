## Level 1
Copying a node is easy; copying its neighbours means copying their neighbours too, and the graph has cycles. What stops the copying from going round a cycle forever, or copying one node twice?

## Level 2
Every original node must end up with exactly one copy, and every list in the copy must point at copies. While you walk the graph, what do you need to look up each time you meet a node?

## Level 3
Keep a map from each original node to its copy, and make the copy the first time you meet the node, before you wire up any lists.
