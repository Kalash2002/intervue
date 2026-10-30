# Daily Temperatures

`temps[i]` is the temperature on day `i`. For each day, you want to know how many days you must wait until a strictly warmer day arrives.

Implement `Forecast.daysToWarmer(int[] temps)` in `Forecast.java`. Return an array of the same length where entry `i` is the number of days from day `i` to the nearest later day with a strictly higher temperature, or `0` if no later day is warmer. Do not modify `temps`.

## Example 1

**Input:** `temps = [20, 18, 25, 19, 19, 30]`
**Output:** `[2, 1, 3, 2, 1, 0]`
**Explanation:** Day 0 waits two days for the 25, day 2 waits three days for the 30, and the last day has no later day.

## Example 2

**Input:** `temps = [5, 5, 5]`
**Output:** `[0, 0, 0]`
**Explanation:** An equal temperature is not warmer, so no day ever finds a warmer one.

## Example 3

**Input:** `temps = []`
**Output:** `[]`
**Explanation:** There are no days, so there is nothing to report.

## Constraints

- `0 <= temps.length <= 2 * 10^5`
- `-60 <= temps[i] <= 60`

## Follow-up

Can you beat O(n²) time, where n is `temps.length`?
