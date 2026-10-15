## Level 1

The window logic is fine; the times going into it are not. Print the times `RequestLog` holds for the sample client and compare them with the file, digit by digit.

## Level 2

`parseMillis` turns the text into a `double` and multiplies by 1000. Most decimal fractions have no exact binary value, so a product such as 2.006 times 1000 can land just below the whole number, and the cast to `long` drops the fraction instead of rounding.

## Level 3

1. Open `LogParser.parseMillis`.
2. Replace the floating-point arithmetic with decimal arithmetic: `BigDecimal` can move the decimal point three places exactly.
3. Ask for an exact `long`, so a time with more than three decimals fails loudly instead of being cut.
4. Run Phase 1 tests: `2.006` should become 2006 and the boundary window should count two requests.
