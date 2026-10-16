## Level 1
When you reach an operator, which values does it work on? Think about which values have been read but not yet used.

## Level 2
An operator always applies to the two most recent unused values. After computing, the result becomes a value that later operators may use. What structure gives you the most recent items first?

## Level 3
Each operator consumes the two values read most recently and leaves one result behind, which later operators may use in turn. Watch which of the two is the left operand, since it matters for subtraction and division, and how a token like "-5" differs from the operator.
