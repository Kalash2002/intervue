# Reconstruct Itinerary

A courier has a stack of one-way plane tickets. Each ticket `[from, to]` is good for one flight from airport `from` to airport `to`, and airports are named by three-letter codes. The courier starts at airport `start` and must use every ticket exactly once, one after another, each flight leaving from the airport where the previous one landed. The same ticket can appear in the stack more than once; each copy is a separate flight that must be used.

Implement `Itinerary.route(String[][] tickets, String start)` in `Itinerary.java`. Return the trip as the list of airports visited, starting with `start`, so it has `tickets.length + 1` entries. When several orders use every ticket, return the alphabetically smallest: compare two trips airport by airport, and the first position where they differ decides, by ordinary alphabetical order of the codes.

Unlike the classic version of this puzzle, the starting airport is passed in rather than fixed.

## Example 1

**Input:** `tickets = [["VNO", "KUN"], ["KUN", "VNO"], ["VNO", "RIX"], ["RIX", "VNO"]]`, `start = "VNO"`
**Output:** `["VNO", "KUN", "VNO", "RIX", "VNO"]`
**Explanation:** Both loops from VNO can go in either order. KUN comes before RIX alphabetically, so the trip flies to KUN first.

## Example 2

**Input:** `tickets = [["LIS", "FAO"], ["LIS", "OPO"], ["OPO", "MAD"], ["MAD", "LIS"]]`, `start = "LIS"`
**Output:** `["LIS", "OPO", "MAD", "LIS", "FAO"]`
**Explanation:** FAO comes first alphabetically, but no ticket leaves FAO, so flying there first would strand the courier with three tickets unused. FAO has to be the last stop.

## Example 3

**Input:** `tickets = [["BCN", "MAD"], ["MAD", "BCN"], ["BCN", "MAD"]]`, `start = "BCN"`
**Output:** `["BCN", "MAD", "BCN", "MAD"]`
**Explanation:** The ticket BCN to MAD is in the stack twice, and both copies are flown.

## Constraints

- `1 <= tickets.length <= 10^5`
- `tickets[i].length == 2`
- Every airport code, `start` included, is three uppercase English letters.
- `tickets[i][0] != tickets[i][1]`
- At least one order starting at `start` uses every ticket exactly once.

## Follow-up

Can you do it without trying an order, finding it stuck and undoing it?
