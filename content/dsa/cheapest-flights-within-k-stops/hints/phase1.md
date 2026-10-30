## Level 1
This looks like a cheapest-route problem, but there is a second limit on the route. Think about whether the cheapest way to reach a city is always the way you want to keep.

## Level 2
The cheapest way to reach a city might use too many flights, while a dearer way uses few and can still go on. What do you have to remember about a city besides its price? Alternatively, think about how the answer improves as you allow one more flight at a time.

## Level 3
For each city, track the cheapest price using at most `r` flights. The table for `r + 1` flights depends only on the table for `r` flights.
