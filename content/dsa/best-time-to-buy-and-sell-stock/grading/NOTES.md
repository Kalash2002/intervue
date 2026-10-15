# Grader notes: Best Time to Buy and Sell Stock

Tests cover: the sample (11), an empty and a one-day list, a falling list (0), a flat list (0), a rising list, a list whose highest price comes before its lowest (`[6, 5, 4, 1, 3]` returns 2; subtracting the global minimum from the global maximum gives 5), a list where the best buy is not the global low (`[2, 12, 1, 6]` returns 10), a best sale on the last day, and two large inputs of 200,000 days (a non-increasing list, and a sawtooth that repeats 0..9999 and returns 9999).

The naive solution tries every pair of days; on the large inputs that is quadratic and times out at the limit and at 3x. The reference makes one pass.
