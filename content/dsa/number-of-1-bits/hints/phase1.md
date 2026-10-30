## Level 1
Writing the pattern out as text and counting the 1s works, if the text really is the 32 bits. Try your idea on a negative input such as -256: which conversions write the bits of the pattern, and which write a minus sign and the bits of 256?

## Level 2
Look at the bits one at a time, moving each to the lowest place with a shift. Java has two right shifts: one brings in zeros at the top and one copies the sign bit. Which one lets a loop "until the value is 0" end for a negative input?

## Level 3
`x & (x - 1)` is `x` with its lowest 1 bit cleared.
