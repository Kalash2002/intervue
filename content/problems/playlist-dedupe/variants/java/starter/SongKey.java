import java.util.Locale;
import java.util.regex.Pattern;

/** Normalising titles and artists, and the key that decides "same song". */
public final class SongKey {
    private SongKey() {}

    private static final Pattern SPACES = Pattern.compile("\\s+");
    private static final Pattern REMASTER_SUFFIX =
            Pattern.compile("\\s*\\([^)]*remaster[^)]*\\)$", Pattern.CASE_INSENSITIVE);

    /** Trimmed, lower case, single spaces, without a trailing "(...remaster...)" part. */
    public static String normalise(String text) {
        String s = text.strip();
        s.toLowerCase(Locale.ROOT);
        s = SPACES.matcher(s).replaceAll(" ");
        s = REMASTER_SUFFIX.matcher(s).replaceAll("");
        return s;
    }

    /** Normalised title and artist; equal keys mean the same song. */
    public static String of(String title, String artist) {
        return normalise(title) + "\u0000" + normalise(artist);
    }
}
