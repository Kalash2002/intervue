## Level 1
Handle the requests in order of start and keep, for every room, the minute it becomes free. At each request there are two questions: which free room has the lowest number, and if none is free, which room frees first?

## Level 2
Those are two different orderings over the rooms: free rooms by number, busy rooms by the time they finish (with the room number as a tie-break). Scanning all rooms each time is too slow when there are many rooms. Which structure gives the smallest element of each ordering quickly?

## Level 3
Keep two heaps: one of free room numbers, one of busy rooms ordered by finishing time and then number. Before placing a request, move every busy room that has finished by its start into the free heap. Count how many sessions each room hosts, and be careful with the type of the finishing times.
