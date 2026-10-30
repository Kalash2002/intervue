import java.util.ArrayList;
import java.util.List;

/** Packs a list of strings into one string and back. Keep no state between calls. */
public class Codec {

    /** Returns one string from which decode can rebuild strings exactly. */
    String encode(List<String> strings) {
        StringBuilder out = new StringBuilder();
        for (String s : strings) {
            out.append(s.length()).append('#').append(s); // length, marker, then the raw content
        }
        return out.toString();
    }

    /** Rebuilds the list that encode was given. */
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
}
