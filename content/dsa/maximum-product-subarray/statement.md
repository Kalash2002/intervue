# Maximum Product Subarray

A test bench chains amplifier stages one after another. Stage `i` multiplies the signal by the whole number `gains[i]`: a negative gain also turns the signal upside down, and a gain of `0` silences it. Any run of one or more consecutive stages can be tapped on its own, and the total gain of that run is the product of its gains.

Implement `Amplifier.bestGain(int[] gains)` in `Amplifier.java` and return the largest total gain of any run, as a `long`. A run may be a single stage, and the answer is negative when every run's product is.

## Example 1

**Input:** `gains = [7,-1,2,-3,-2,4]`
**Output:** `48`
**Explanation:** Stages 2 to 5 give 2 × -3 × -2 × 4 = 48. The whole chain has three negative gains, so its product is negative. Leaving out everything up to the first negative gain (the 7 and the -1) beats leaving out everything from the last one (the -2 and the 4), which gives 7 × -1 × 2 × -3 = 42.

## Example 2

**Input:** `gains = [3,-5,2,-2]`
**Output:** `60`
**Explanation:** The two negative gains cancel, so the whole chain gives 3 × -5 × 2 × -2 = 60. The best run ending at the 2 is the 2 alone, but the product ending there that leads to 60 is the most negative one, -30.

## Example 3

**Input:** `gains = [-6,5,0,2,-1,3]`
**Output:** `5`
**Explanation:** A run that includes the silenced stage has a product of 0. On the left of it the best run is the 5 alone; on the right it is the 3 alone.

## Example 4

**Input:** `gains = [-7]`
**Output:** `-7`
**Explanation:** The only run is the single stage, so the answer is negative.

## Constraints

- `1 <= gains.length <= 10^5`
- `-10 <= gains[i] <= 10`
- The product of every run is below `2^63` in absolute value.

## Follow-up

What if you also had to say where the best run starts and ends?
