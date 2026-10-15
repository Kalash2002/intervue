# Daily Temperatures

`temps[i]` is the temperature on day `i`. For each day, you want to know how many days you must wait until a strictly warmer day arrives.

Implement `Forecast.daysToWarmer(int[] temps)` in `Forecast.java`. Return an array of the same length where entry `i` is the number of days from day `i` to the nearest later day with a strictly higher temperature, or `0` if no later day is warmer.

## Examples

- `temps = [20, 18, 25, 19, 19, 30]` returns `[2, 1, 3, 2, 1, 0]`: day 0 waits two days for 25, day 2 waits three days for 30, and the last day has no later day.
- `temps = [5, 5, 5]` returns `[0, 0, 0]`: equal is not warmer.
- `temps = []` returns `[]`: there are no days.

## Constraints

- 0 <= `temps.length` <= 200,000
- -60 <= `temps[i]` <= 60
- The input array must not be modified
