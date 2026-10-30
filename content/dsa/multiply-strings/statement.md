# Multiply Strings

A calculator for numbers far longer than 64 bits keeps every number as a string of decimal digits. Given two non-negative integers `num1` and `num2` in that form, return their product in the same form: decimal digits with no leading zeros, and `"0"` when the product is zero.

Implement `Multiplier.multiply(String num1, String num2)` in `Multiplier.java`. Work with the digits: do not convert either number as a whole into an `int`, a `long`, a `BigInteger`, a `BigDecimal` or any other number type. A `long` holds at most 19 digits, and the numbers here have up to 200.

## Example 1

**Input:** `num1 = "4096"`, `num2 = "735"`
**Output:** `"3010560"`
**Explanation:** 4096 × 700 = 2867200 and 4096 × 35 = 143360, which add up to 3010560.

## Example 2

**Input:** `num1 = "99999"`, `num2 = "9999"`
**Output:** `"999890001"`
**Explanation:** Every pair of digits multiplies to 81, so carries run through every position. As a check, 99999 × 9999 = 999990000 - 99999.

## Example 3

**Input:** `num1 = "0"`, `num2 = "58173"`
**Output:** `"0"`
**Explanation:** Zero times any number is 0, written as one digit, not as a row of zeros.

## Constraints

- `1 <= num1.length(), num2.length() <= 200`
- `num1` and `num2` hold only decimal digits.
- Neither number has a leading zero, except the number `0` itself.

## Follow-up

How would you multiply two numbers with a million digits each?
