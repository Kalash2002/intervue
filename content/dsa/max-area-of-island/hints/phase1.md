## Level 1
Measuring the island around every land cell gives the right answer, but on a map that is one huge island it measures the same island hundreds of thousands of times. How could each island be measured only once?

## Level 2
Scan the map; at land nothing has claimed yet, spread out to every connected land cell and count them as you go. What must the spreading leave behind so the scan skips those cells later, and what limits how deep it may go?

## Level 3
Count the cells as they come off an explicit stack of cells to visit, marking each when it is pushed; recursion one level per cell runs out of stack on a long island.
