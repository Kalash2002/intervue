## Level 1
Two addresses are "the same" when they reduce to the same plain form. What is that plain form, and which part of the address does each rule touch?

## Level 2
Reduce each address on its own to a canonical text, without looking at the others. Then the problem is only counting distinct texts. Which collection tells you in one step whether you have seen a value before?

## Level 3
Split at the `@`. In the name, cut at the first `+` and drop the dots; leave the domain untouched. Put the rebuilt address into a set and return its size.
