## Level 1

The total held on an account only changes when a hold starts or ends. You never need to look at minutes where nothing happens.

## Level 2

Use a sweep: turn every hold into two events, an increase at its start and a decrease at its end, sort the events by minute, and walk them while keeping a running total. When an end and a start fall on the same minute, the end must come first; that is what makes touching holds not overlap.

## Level 3

1. Collect the active holds of the account; return the empty answer when there are none.
2. Build the events and sort by minute, ends before starts within one minute.
3. Walk the events with a running total of the same type as the amounts.
4. For `peak`, replace the best only when the total is strictly greater, so a tie keeps the earliest minute.
5. For `firstBreach`, return the first minute where the total is strictly greater than the limit, and reject a negative limit before doing any work.
