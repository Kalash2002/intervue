# Target Sum

Put a plus or a minus sign in front of every number in `nums`, keep them in their order, and add them up. Count the choices of signs whose total is exactly `target`. Two choices are different when at least one position has a different sign, so a `0` counts both ways: `+0` and `-0` are two different choices.

Implement `Signs.ways(int[] nums, int target)` in `Signs.java` and return the count as a `long`. With up to 60 numbers it can be as large as 2^60, far past what an `int` holds.

## Example 1

**Input:** `nums = [4,1,3,6,2]`, `target = 2`
**Output:** `3`
**Explanation:** +4 -1 +3 -6 +2, -4 +1 -3 +6 +2 and -4 -1 +3 +6 -2 all make 2, and no other choice of the 32 does.

## Example 2

**Input:** `nums = [0,6,0,2]`, `target = -4`
**Output:** `4`
**Explanation:** -6 + 2 makes -4, and each 0 can take either sign, so that one way of signing 6 and 2 gives 2 × 2 = 4 choices.

## Example 3

**Input:** `nums = [3,8]`, `target = -12`
**Output:** `0`
**Explanation:** The smallest total any choice can reach is -3 - 8 = -11, so -12 is out of reach.

## Constraints

- `1 <= nums.length <= 60`
- `0 <= nums[i] <= 1000`
- `sum(nums) <= 1000`
- `-1000 <= target <= 1000`
- The answer is at most `2^60`, so it fits a `long`.

## Follow-up

What if each number could also be left out, so that it counted as plus, minus or nothing?
