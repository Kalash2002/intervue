# Unique Email Addresses

A mail provider treats several spellings of the part before the `@` as one mailbox. An address is written `name@domain`, with exactly one `@`. Two spelling rules apply to the **name** only; the domain is always compared exactly as written.

- A `.` in the name is decoration and is dropped.
- The first `+` in the name starts a label. That `+`, and everything after it up to the `@`, is dropped.

Two addresses reach the same mailbox when, after these rules, their names are equal and their domains are equal.

Implement `Mailboxes.countDistinct(String[] emails)` in `Mailboxes.java`. It returns how many different mailboxes the addresses reach.

## Example 1

**Input:** `emails = ["sam.k@mail.org", "samk+news@mail.org", "sa.mk@mail.org"]`
**Output:** `1`
**Explanation:** The dots go from the first and third names and the label goes from the second, so all three become `samk@mail.org`.

## Example 2

**Input:** `emails = ["lee@a.com", "lee@b.com", "lee@ac.om"]`
**Output:** `3`
**Explanation:** Dots in the domain are kept, so `a.com`, `b.com` and `ac.om` are three different places.

## Example 3

**Input:** `emails = ["ann+1@z.net", "ann+2@z.net", "a.nnb@z.net"]`
**Output:** `2`
**Explanation:** The first two become `ann@z.net`, and the third becomes `annb@z.net`.

## Example 4

**Input:** `emails = ["a.+x@k.io", "a@k.io", "a.+@k.org"]`
**Output:** `2`
**Explanation:** The dot before the `+` is dropped and the label `x` is ignored, so the first two both reach `a@k.io`. The third has a different domain.

## Constraints

- `0 <= emails.length <= 10^5`
- `3 <= emails[i].length() <= 100`
- `emails[i]` has exactly one `@`.
- The name before the `@` holds only lowercase letters, `.` and `+`.
- The name starts with a letter.
- The domain after the `@` holds only lowercase letters and `.`.
- The domain starts and ends with a letter.

## Follow-up

How would you return the groups of addresses that reach the same mailbox?
