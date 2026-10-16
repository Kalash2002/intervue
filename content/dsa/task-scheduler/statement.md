# Task Scheduler

A label printer handles one job per time slot. Each job has a kind, written as an uppercase letter. After a job of some kind runs, at least `gap` slots must pass before another job of the same kind may run. A slot may be left idle, and the jobs can be done in any order.

Implement `Printer.minSlots(char[] jobs, int gap)` in `Printer.java`. Return the smallest number of slots in which all the jobs can be finished. An empty list needs `0` slots.

Here `gap` may be as large as 3000, much larger than the 26 possible kinds, so most of a schedule can be idle slots and the answer can reach about 300 million. It still fits in an `int`.

## Example 1

**Input:** `jobs = {'A','A','A','B','B','C'}`, `gap = 2`
**Output:** `7`
**Explanation:** One schedule is A B C A B (idle) A. The three A jobs sit in slots 1, 4 and 7, two slots apart, and no schedule can finish in fewer than 7 slots.

## Example 2

**Input:** `jobs = {'P','P','Q','Q','R'}`, `gap = 1`
**Output:** `5`
**Explanation:** The schedule P Q R P Q keeps equal kinds apart with no idle slot, so the answer is the number of jobs.

## Example 3

**Input:** `jobs = {'K','K','K','K'}`, `gap = 3`
**Output:** `13`
**Explanation:** The four K jobs must sit in slots 1, 5, 9 and 13, with three idle slots between each pair.

## Example 4

**Input:** `jobs = {}`, `gap = 5`
**Output:** `0`
**Explanation:** With no jobs nothing needs a slot.

## Constraints

- `0 <= jobs.length <= 10^5`
- `jobs[i]` is an uppercase letter from `A` to `Z`.
- `0 <= gap <= 3000`

## Follow-up

How would you output an actual schedule, not just its length?
