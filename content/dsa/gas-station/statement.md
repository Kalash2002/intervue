# Gas Station

An electric shuttle drives a loop past `n` charging stops, numbered `0` to `n - 1` in driving order. At stop `i` it takes on `charge[i]` units, and the leg from stop `i` to the next stop (stop `i + 1`, or stop `0` after the last one) uses `drain[i]` units. The battery has no upper limit.

The shuttle starts with an empty battery at a stop you choose, charges there, and drives once round the loop back to that stop, charging at every stop on the way. It may arrive at a stop with an empty battery, but it must never run out partway along a leg.

Implement `Shuttle.startStop(int[] charge, int[] drain)` in `Shuttle.java` and return a stop from which the shuttle can complete the loop, or `-1` if there is none. If several stops work, return the lowest-numbered one.

## Example 1

**Input:** `charge = [6,9,0,5,6,5]`, `drain = [8,9,7,3,1,2]`
**Output:** `3`
**Explanation:** From stop 3 the battery after each leg reads 2, 7, 10, 8, 8 and 1, never below zero, and no other stop works. Stop 1 has the most charge, but it arrives at stop 2 empty and cannot cover the leg of 7 from there; stop 4 gains the most on its own leg (6 - 1 = 5), but it runs out on the leg from stop 2 as well.

## Example 2

**Input:** `charge = [4,2,6,1]`, `drain = [3,5,2,4]`
**Output:** `-1`
**Explanation:** The loop uses 14 units and the stops give only 13, so the shuttle runs out wherever it starts.

## Example 3

**Input:** `charge = [2,5,1]`, `drain = [1,3,2]`
**Output:** `0`
**Explanation:** Stops 0 and 1 both work: from stop 0 the battery reads 1, 3 and 2, and from stop 1 it reads 2, 1 and 2. Stop 2 runs out on its first leg. The lower of the two is 0.

## Example 4

**Input:** `charge = [4]`, `drain = [4]`
**Output:** `0`
**Explanation:** With one stop, the loop is a single leg from stop 0 back to itself. The shuttle arrives with an empty battery, which is allowed.

## Constraints

- `1 <= charge.length <= 10^5`
- `drain.length == charge.length`
- `0 <= charge[i] <= 10^4`
- `0 <= drain[i] <= 10^4`
- `sum(charge) <= 10^9`
- `sum(drain) <= 10^9`

## Follow-up

What if you had to list every stop that works?
