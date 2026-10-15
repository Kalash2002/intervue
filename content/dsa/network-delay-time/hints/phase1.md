## Level 1
Each node receives the signal at the earliest time any route can bring it. If you knew that time for every node, what would the answer be?

## Level 2
The delays are not all equal, so counting links does not give the earliest time. Which node's earliest time can you be sure of right now? Once you are sure of it, what does it tell you about its neighbours?

## Level 3
Always settle the not-yet-settled node with the smallest known time, then try to improve the times of the nodes it links to. A priority queue gives you that node quickly. When the queue runs out, check whether every node was settled.
