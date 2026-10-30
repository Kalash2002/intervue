## Level 1
Cutting as soon as the current letter has no copies left is too early when an earlier letter of the piece still appears later, and cutting before every new letter fails the same way. Checking each possible cut by comparing the letters on its two sides works, but it reads the whole string for every cut. Ask what decides whether one particular cut is allowed.

## Level 2
A cut after position `i` is allowed exactly when no letter appears both before and after it. Does whether that cut is allowed depend on any other cut you make?

## Level 3
Knowing where each letter last appears tells you how far the piece you are in has to reach at least.
