## Level 1
This looks like a cheapest-route problem, but there is a second limit on the route. Think about whether the cheapest way to reach a city is always the way you want to keep.

## Level 2
The cheapest way to reach a city might use too many flights, while a dearer way uses few and can still go on. What do you have to remember about a city besides its price? Alternatively, think about how the answer improves as you allow one more flight at a time.

## Level 3
Allow the trip one flight at a time. Keep the cheapest known price to each city using at most that many flights. To go from one round to the next, extend every flight from the prices of the previous round only, never from prices you have just updated in the same round.
