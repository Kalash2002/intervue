# Koko Eating Bananas

A print shop has a queue of jobs, where `jobs[i]` is the number of pages in job `i`, and `hours` hours before the deadline. The printer runs at a fixed whole number of pages per hour, the rate, chosen once. Each hour it works on a single job: it prints up to `rate` pages of that job, and if the job has fewer pages left, it finishes the job and stays idle for the rest of that hour. It never starts a second job in the same hour.

Return the smallest rate at which every job is printed within `hours` hours. Implement `PrintRate.minRate(int[] jobs, int hours)` in `PrintRate.java`.

## Example 1

**Input:** `jobs = [7, 15, 4, 11]`, `hours = 8`
**Output:** `6`
**Explanation:** At 6 pages an hour the jobs take 2, 3, 1 and 2 hours, 8 in all. At 5 they take 2, 3, 1 and 3, which is 9 and too many.

## Example 2

**Input:** `jobs = [7, 15, 4, 11]`, `hours = 4`
**Output:** `15`
**Explanation:** Four jobs in four hours leaves one hour per job, so the rate must cover the largest job, 15 pages.

## Example 3

**Input:** `jobs = [1000000000, 1000000000, 1000000000]`, `hours = 2000000000`
**Output:** `2`
**Explanation:** At 1 page an hour the jobs need 3,000,000,000 hours, more than allowed and more than an `int` can count. At 2 they need 1,500,000,000.

## Constraints

- `1 <= jobs.length <= 10^4`
- `1 <= jobs[i] <= 10^9`
- `jobs.length <= hours <= 2 * 10^9`

## Follow-up

Can you find the rate in O(n log m) time, where m is the largest job?
