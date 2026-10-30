# Grader notes: Add Two Numbers

Tests cover: the sample, a carry that becomes an extra digit (16 + 84 and 9 + 9), zero plus a number in both orders, single digits without a carry, a carry that runs through four nines of the longer list, a longer first list, numbers too big for a `long` (thirty nines plus one, and two 21-digit numbers), and two numbers of 10,000 and 9,000 random digits (fixed seed) whose sum the test computes with `BigInteger`. Every test checks, after the call, that each input node still holds its digit and its link.

Traps from the walkthrough and hints, and the tests that grade them:
- Converting to `long`: a scratch version fails `numbersTooBigForALong` and `longNumbers`.
- Dropping the last carry: a scratch version that loops only while a list has nodes fails `carryMakesAnExtraDigit` and `numbersTooBigForALong`.
- Writing the sum into the first list's nodes: a scratch version fails every test.
- Recursion on long numbers: a scratch version of the walkthrough's Better passes every small test and fails `longNumbers` with `StackOverflowError` at 10,000 digits.

The walkthrough's Brute force (`BigInteger`) passes every test (64 ms on `longNumbers`).

No naive solution: the brute force is not meaningfully slower at these sizes, so time is not graded separately for this question (ADR 0008). The 1-second limit on `longNumbers` is generous; the reference finishes in about 50 ms.
