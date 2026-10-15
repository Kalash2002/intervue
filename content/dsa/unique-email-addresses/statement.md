# Unique Email Addresses

A mail provider delivers to the same mailbox even when the name part of an address is written in several ways. An address is `name@domain`, with exactly one `@`. Two rules apply to the **name** only; the domain is always compared exactly as written.

- Every `.` in the name is ignored.
- The first `+` in the name, and every character after it up to the `@`, is ignored.

Two addresses reach the same mailbox when, after these rules, their names are equal and their domains are equal.

Implement `Mailboxes.countDistinct(String[] emails)` in `Mailboxes.java`. It returns how many different mailboxes the addresses reach.

## Examples

- `["sam.k@mail.org", "samk+news@mail.org", "sa.mk@mail.org"]` returns `1`: all three names become `samk`.
- `["lee@a.com", "lee@b.com", "lee@ac.om"]` returns `3`: dots in the domain are kept, so `a.com`, `b.com` and `ac.om` are different.
- `["ann+1@z.net", "ann+2@z.net", "a.nnb@z.net"]` returns `2`: the first two become `ann`, the third becomes `annb`.

## Constraints

- 0 <= `emails.length` <= 100,000
- Each address is 3 to 100 characters: lowercase letters, `.` and `+` in the name, lowercase letters and `.` in the domain, and exactly one `@`.
- The name starts with a letter, and the domain starts and ends with a letter.
