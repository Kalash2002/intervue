# Grader notes: Task Scheduler

Tests cover: the sample, an empty list, a single job, no gap, a gap smaller than the number of kinds so no slot is idle, kinds that tie for the highest count, one dominant kind among many small ones, a gap larger than the job count, and a large case of 100,000 identical jobs with gap 3,000, whose answer is above three hundred million slots.

The naive solution simulates the printer one slot at a time, scanning all 26 kinds for the busiest one that is allowed to run. Its work grows with the answer, which here is about 300 million slots, so it exceeds the limit and 3x the limit. The reference counts the kinds and computes the answer in one pass over the jobs.
