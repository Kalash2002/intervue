# House Robber

Houses stand in a row along a street, and house `i` holds `cash[i]`. A burglar wants to empty some of them in one night, but neighbouring houses share an alarm: emptying two houses next to each other sets it off. Houses that are not next to each other are safe to empty together, however many there are.

Implement `Street.maxLoot(int[] cash)` in `Street.java` and return the largest total that can be taken without emptying two neighbouring houses. Taking nothing is allowed.

## Example 1

**Input:** `cash = [6,1,2,9,4]`
**Output:** `15`
**Explanation:** Empty houses 0 and 3 for 6 + 9. Taking every other house from the start gives only 6 + 2 + 4 = 12.

## Example 2

**Input:** `cash = [3,10,3,1,8]`
**Output:** `18`
**Explanation:** Empty houses 1 and 4 for 10 + 8, skipping two houses in a row between them. Neither the even houses (14) nor the odd houses (11) do as well.

## Example 3

**Input:** `cash = [7]`
**Output:** `7`
**Explanation:** A single house has no neighbours, so it is emptied.

## Constraints

- `1 <= cash.length <= 1000`
- `0 <= cash[i] <= 10^6`

## Follow-up

What if you also had to return which houses were chosen?
