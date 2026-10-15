import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Compares the track lists of two playlists. */
public class Playlists {

    /** Returns each value present in both arrays once, in ascending order. */
    static int[] shared(int[] mine, int[] yours) {
        Set<Integer> mineSet = new HashSet<>();
        for (int track : mine) {
            mineSet.add(track);
        }
        List<Integer> found = new ArrayList<>();
        for (int track : yours) {
            if (mineSet.remove(track)) {
                found.add(track);
            }
        }
        int[] result = new int[found.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = found.get(i);
        }
        Arrays.sort(result);
        return result;
    }
}
