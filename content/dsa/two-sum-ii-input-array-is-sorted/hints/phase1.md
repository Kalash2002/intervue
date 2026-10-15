## Level 1
Checking every pair works but is slow. What does the fact that the array is sorted tell you about the sum of a pair when you move one of its two positions?

## Level 2
Put one position at the smallest value and one at the largest. If their sum is too big, which of the two should move, and in which direction? What if it is too small?

## Level 3
Start with the two ends. Each step, compare the sum with the target and move exactly one position inwards, the one whose move can fix the sum. Think about why the pair you are looking for is never jumped over.
