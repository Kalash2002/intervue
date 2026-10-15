## Phase 2: suggestions

Implement in `Suggester`:

```java
List<Merchant> suggest(String typed, int limit)
```

Rules:

- Normalize the typed text with `Normalizer`. If nothing is left, return an empty list.
- A merchant matches when the typed text is the beginning of its normalized name, **or the beginning of the name from any later word on**. For "Cafe Coffee Day": `caf`, `coffee`, `coffee d` and `day` match; `offee` and `coffee cafe` do not.
- Order matches by `txnCount`, highest first. For equal counts, order by normalized name.
- Return at most `limit` merchants. Each merchant appears once.
- `limit` must be between 1 and 10; otherwise throw `IllegalArgumentException`.
- Suggestions always reflect the directory as it is now, including merchants added or replaced after earlier calls.

Run **Phase 2 tests**.
