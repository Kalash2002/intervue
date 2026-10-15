import java.util.Arrays;

/** Compares the track lists of two playlists. */
public class Playlists {

    /** Returns each value present in both arrays once, in ascending order. */
    static int[] shared(int[] mine, int[] yours) {
        int[] result = new int[Math.min(mine.length, yours.length)];
        int count = 0;
        for (int track : mine) {
            boolean listed = false;
            for (int i = 0; i < count; i++) {
                if (result[i] == track) {
                    listed = true;
                    break;
                }
            }
            if (listed) {
                continue;
            }
            for (int other : yours) {
                if (other == track) {
                    result[count++] = track;
                    break;
                }
            }
        }
        int[] sorted = Arrays.copyOf(result, count);
        Arrays.sort(sorted);
        return sorted;
    }
}
