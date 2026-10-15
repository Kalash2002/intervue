# Task Scheduler

A label printer handles one job per time slot. Each job has a kind, written as an uppercase letter. After a job of some kind runs, at least `gap` slots must pass before another job of the same kind may run. A slot may be left idle, and the jobs can be done in any order.

Implement `Printer.minSlots(char[] jobs, int gap)` in `Printer.java`. Return the smallest number of slots in which all the jobs can be finished. An empty list needs `0` slots.

## Examples

- `jobs = {'A','A','A','B','B','C'}`, `gap = 2` returns `7`: A B C A B (idle) A.
- `jobs = {'A','A','B','B','C','C'}`, `gap = 1` returns `6`: A B C A B C needs no idle slot.
- `jobs = {'A','A','A','A'}`, `gap = 3` returns `13`: the four A jobs sit in slots 1, 5, 9 and 13.

## Constraints

- 0 <= `jobs.length` <= 100,000
- Every job is an uppercase letter from `A` to `Z`.
- 0 <= `gap` <= 3,000
