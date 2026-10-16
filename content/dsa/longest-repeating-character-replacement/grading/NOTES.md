# Grader notes: Longest Repeating Character Replacement

Tests cover: the sample (5), an empty string, `k = 0` (the longest existing run), `k` larger than the string, a whole string that fits exactly (`"TUUTU"`, `k = 2` returns 5: three Us and two Ts) and one that falls one short (`k = 1` returns 4: `"UUTU"`), a majority letter that is not the first letter of the window (`"CABBBBAC"`, `k = 2` returns 6; the window that wins has its majority letter strictly inside), the best window in the middle of the string with a different letter from the rest (hand-checked: `"CCABBBBABBBBACC"`, `k = 1` returns 9), and two large inputs of 100,000 characters (one repeated letter with `k = 0`, and alternating letters with `k = 49,999`, which returns 99,999).

The naive solution extends from every start and stops when more than `k` changes are needed; on the large inputs every start runs nearly to the end, which is quadratic and times out at the limit and at 3x. The reference uses one window.
