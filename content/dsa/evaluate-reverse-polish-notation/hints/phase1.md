## Level 1
When you reach an operator, which values does it work on? Think about which values have been read but not yet used.

## Level 2
An operator always applies to the two most recent unused values. After computing, the result becomes a value that later operators may use. What structure gives you the most recent items first?

## Level 3
Read tokens left to right. Push every number; at an operator, take the top value as the right operand and the one below it as the left operand, compute, and push the result. Be careful with a token such as a minus sign followed by digits, which is a number, and with how division rounds for negative results.
