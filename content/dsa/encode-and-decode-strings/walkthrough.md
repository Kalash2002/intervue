# How a strong round goes: Encode and Decode Strings

## Approach

The hard part is the decoder: it must find where each string ends. A separator such as a comma fails the moment a string contains one, and escaping it makes the decoder more tricky than it needs to be. So I stop searching for the end and write it down instead. Before every string I put its length, then a marker, then the string itself. The decoder reads digits up to the marker, which gives the length, and then takes exactly that many characters without looking at them. The content may hold digits, markers or anything else, because the decoder never reads it as structure. An empty list encodes to the empty text, and a list with one empty string encodes to `0#`, so the two stay different. Nothing is stored on the object, so any instance can decode what another encoded.

```java
String encode(List<String> strings) {
    StringBuilder out = new StringBuilder();
    for (String s : strings) {
        out.append(s.length()).append('#').append(s); // length, marker, then the raw content
    }
    return out.toString();
}

List<String> decode(String text) {
    List<String> result = new ArrayList<>();
    int pos = 0;
    while (pos < text.length()) {
        int marker = text.indexOf('#', pos); // the first marker after pos is ours: the prefix is only digits
        int length = Integer.parseInt(text.substring(pos, marker));
        pos = marker + 1;
        result.add(text.substring(pos, pos + length)); // take exactly length characters, whatever they are
        pos += length;
    }
    return result;
}
```

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Join with a separator (wrong when a string holds it) | O(n) | O(n) |
| Join with escaping | O(n) | O(n) |
| Length prefix | O(n) for n total characters | O(n) |

## Where it shows up

Length-prefixed fields are how many wire formats are framed: network protocols, binary file formats and message queues write a size before a payload so the reader knows how much to take. Storing several values in one text column or one cache entry has the same problem, and the same fix.

## Follow-up answers

**How would you change it to read the encoded text as a stream, without holding all of it in memory?** The format already allows it. Read characters until the marker to get the length, then read that many characters and hand the string on. Only one string is in memory at a time.

**What if the strings were bytes, not characters?** Write the length as a fixed-width integer, say four bytes, instead of digits and a marker. The decoder then reads four bytes, then that many bytes. The prefix has a fixed size, so no marker is needed at all.

**How would you version the format so older encoded data stays readable?** Start the encoded data with a version number, written in the same length-prefixed style. The decoder reads it first and picks the matching reader, so a new format never has to guess whether old data is new.
