# Grader notes: Valid Palindrome

Tests cover: the sample, the empty string, a string with only punctuation and spaces, a single character, mismatches (with and without punctuation), digits that must be compared (the trap for solutions that keep only letters), upper and lower case, punctuation between letters and at the ends, and two large inputs of 500,000 characters (a palindrome with punctuation sprinkled in, and the same with one mismatch in the middle).

No naive solution: building a cleaned copy and reversing it is also linear, so there is no meaningfully slower correct approach, and time is not graded separately for this question (ADR 0008). The large tests only guard against quadratic string building.
