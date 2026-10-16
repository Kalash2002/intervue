# Encode and Decode Strings

A small service has to send a list of strings through a channel that carries only one string. Write the two halves of that trip.

Implement the class `Codec` in `Codec.java` with two instance methods:

- `String encode(List<String> strings)` packs the list into a single string.
- `List<String> decode(String text)` turns that string back into the original list.

For every list `L`, `decode(encode(L))` must equal `L`: the same strings, in the same order, with the same number of entries. Any encoding is allowed, and the encoded text does not have to be readable. The two methods may be called on different `Codec` instances, so the object must not remember anything between calls.

## Example 1

**Input:** `strings = ["red", "green"]`
**Output:** `["red", "green"]` (what `decode(encode(strings))` returns)
**Explanation:** The two words go in and the same two come back, in the same order. Any encoding that lets the decoder tell where `red` stops and `green` starts will do.

## Example 2

**Input:** `strings = ["3#ab", "#"]`
**Output:** `["3#ab", "#"]`
**Explanation:** The first string contains a digit and a `#`, which look like the length and marker of a length-prefix scheme, and the second is a lone `#`. The strings must come back unchanged, so the decoder can never trust the content to be free of any particular character.

## Example 3

**Input:** `strings = [""]`
**Output:** `[""]`
**Explanation:** The list has one entry, and that entry is the empty string. It must not come back as an empty list, nor as two entries; `[]` and `[""]` are different lists and must decode to different results.

## Constraints

- `0 <= strings.size() <= 10^5`
- `0 <= total characters over all strings <= 2 * 10^6`
- A string may be empty and may contain any character: digits, `#`, commas, spaces, newlines, accents or emoji.

## Follow-up

Can the decoder rebuild the list in a single left-to-right pass over the text?
