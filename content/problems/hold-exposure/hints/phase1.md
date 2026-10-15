## Level 1

The wrong number appears only for very large accounts. Look at how `exposureAt` adds up the amounts, and at the type of every variable that holds money along the way.

## Level 2

Amounts are stored as `long` paise, but the running total is a smaller type. A compound assignment such as `+=` converts the result back to the variable's type without any warning, so a total above about 2.1 billion wraps around to a negative number.

## Level 3

1. Find the variable that accumulates the total inside `exposureAt`.
2. Change its type so it matches the type of the amounts it adds.
3. Run Phase 1 tests: the corporate sample should now pass along with the retail ones.
4. Be ready to explain why the compiler accepted the narrowing: compound assignment includes an implicit cast.
