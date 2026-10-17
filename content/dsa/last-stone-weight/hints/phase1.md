## Level 1
Simulate the game exactly as described; there is no shortcut in the rules. The question is what each turn costs you. What do you need to find at the start of every turn?

## Level 2
Every turn removes the two heaviest stones and may add one new, lighter stone. Looking through all the stones every turn is too slow at this size. Which structure keeps its largest value ready while you keep adding and removing?

## Level 3
A max-heap answers "heaviest" in constant time and absorbs the new stone in logarithmic time, so each turn costs a logarithm instead of a full pass.
