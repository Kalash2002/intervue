## Level 1
If you could compare two trees for equality, where in `root` would you need to try that comparison?

## Level 2
Trying the comparison at every node is correct, but on long runs of equal values each attempt can walk far before it fails. Also remember the input may be one long path, deeper than the call stack.

## Level 3
Write each tree as a flat sequence that keeps its shape; then a subtree of `root` is a block of consecutive entries, and finding `sub` is searching for one sequence inside another.
