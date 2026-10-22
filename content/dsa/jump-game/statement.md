# Jump Game

A row of platforms is numbered from 0, and you start on platform 0. Platform `i` has a spring of strength `spring[i]`: from it you can jump forward by any whole number of platforms from 1 up to `spring[i]`, as long as you land on a platform. A spring of strength 0 cannot launch you at all.

Implement `Platforms.canReachEnd(int[] spring)` in `Platforms.java` and return `true` if some sequence of jumps takes you from platform 0 to the last platform, `false` otherwise.

## Example 1

**Input:** `spring = [3,1,4,0,0,0,2]`
**Output:** `true`
**Explanation:** Jump from platform 0 to platform 2, whose spring of 4 reaches the last platform, 6. The longest jump from platform 0 lands on platform 3, which has no spring, so jumping as far as you can is not always right.

## Example 2

**Input:** `spring = [1,2,0,3,0,0,0,1]`
**Output:** `false`
**Explanation:** Platforms 0 to 6 can all be reached, but no spring among them goes past platform 6, and platforms 4 to 6 have none at all, so platform 7 cannot be reached.

## Example 3

**Input:** `spring = [0,6]`
**Output:** `false`
**Explanation:** You can never leave platform 0, so the strong spring on platform 1 never comes into play.

## Constraints

- `1 <= spring.length <= 10^5`
- `0 <= spring[i] <= 10^5`

## Follow-up

What if you had to return the fewest jumps instead?
