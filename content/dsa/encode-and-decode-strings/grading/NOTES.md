# Grader notes: Encode and Decode Strings

Tests cover: a round trip of ordinary words, the empty list, a single empty string and several empty strings (each must come back with the same size), strings made of separators, digits and length-looking text such as `3#abc` (the trap for join-with-a-delimiter solutions), accents, CJK text and an emoji pair, control characters and newlines, decoding on a fresh `Codec` instance, different lists giving different encodings (including `[]` and `[""]`), and a round trip of 100,000 strings with a generous limit.

No naive solution: there is no meaningfully slower correct approach, so time is not graded separately for this question (ADR 0008). The large test only guards against quadratic string building.
