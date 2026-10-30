# Grader notes: K Closest Points to Origin

Every test sorts the returned points by x, then y, and compares them with the expected points, so any order passes. They cover: the sample, a tie inside the answer (two points at squared distance 8), squared distances past the `int` range (`{60000, 0}` against `{30000, -30000}`, and three points near the corners of the grid where the two smaller distances also pass `2^31`; a version that computes `x * x + y * y` in `int` picks the wrong points), `k` equal to the number of points, a single point, repeated points that count separately, the origin and points on the axes, and a large test.

The large test has 100,000 points. Point `i` (from 1) has coordinates `i` and `i - 1` in some order and with random signs, so its squared distance `i^2 + (i - 1)^2` grows with `i` and the `k` closest are exactly points 1..k; the largest distances pass `2^31`. The test is shuffled with a fixed seed and asks for `k` = 1, 777, 50,000, 99,999 and 100,000, checking each returned point against that rule.

No naive solution: sorting all the points by distance is only a log factor slower than the heap or quickselect at this size, too small a gap to time reliably, so time is not graded separately for this question (ADR 0008). The large test still has a limit, which the sort, the heap and the reference's quickselect all meet.
