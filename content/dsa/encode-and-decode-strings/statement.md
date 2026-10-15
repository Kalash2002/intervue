# Encode and Decode Strings

A small service has to send a list of strings through a channel that carries only one string. Write the two halves of that trip.

Implement the class `Codec` in `Codec.java` with two instance methods:

- `String encode(List<String> strings)` packs the list into a single string.
- `List<String> decode(String text)` turns that string back into the original list.

For every list `L`, `decode(encode(L))` must equal `L`: the same strings, in the same order, with the same number of entries. Any encoding is allowed, and the encoded text does not have to be readable. The two methods may be called on different `Codec` instances, so the object must not remember anything between calls.

## Examples

- `["red", "green"]` is encoded, then decoded, and comes back as `["red", "green"]`.
- `[]` and `[""]` are different lists: the first has no entries and the second has one empty string. They must decode to different results.
- `["3#ab", "#"]` holds text that looks like a separator and a length. It must come back unchanged, so the content can never be trusted to be free of any particular character.

## Constraints

- 0 <= `strings.size()` <= 100,000
- 0 <= the total number of characters over all strings <= 2,000,000
- A string may be empty and may contain any character: digits, `#`, commas, spaces, newlines, accents or emoji.
