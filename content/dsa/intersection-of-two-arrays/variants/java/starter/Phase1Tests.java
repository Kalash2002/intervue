import static kit.Check.assertEquals;

import java.util.Arrays;
import kit.Test;

public class Phase1Tests {

    private static void check(String expected, int[] actual) {
        assertEquals(expected, Arrays.toString(actual));
    }

    @Test(order = 1)
    void sample() {
        check("[3, 7]", Playlists.shared(new int[] {7, 3, 9, 3}, new int[] {3, 8, 3, 7, 7}));
    }

    @Test(order = 2)
    void disjointLists() {
        check("[]", Playlists.shared(new int[] {1, 2, 3}, new int[] {4, 5, 6}));
    }

    @Test(order = 3)
    void emptyOnEitherSide() {
        check("[]", Playlists.shared(new int[] {}, new int[] {1, 2}));
        check("[]", Playlists.shared(new int[] {1, 2}, new int[] {}));
        check("[]", Playlists.shared(new int[] {}, new int[] {}));
    }

    @Test(order = 4)
    void repeatsAreListedOnce() {
        check("[1, 5]", Playlists.shared(new int[] {5, 5, 1}, new int[] {1, 5, 5}));
    }

    @Test(order = 5)
    void negativeAndExtremeValues() {
        check("[-2147483648, -5, 5]",
                Playlists.shared(
                        new int[] {-5, 0, 5, Integer.MIN_VALUE},
                        new int[] {5, -5, -6, Integer.MIN_VALUE, Integer.MAX_VALUE}));
    }

    @Test(order = 6)
    void singleElementLists() {
        check("[4]", Playlists.shared(new int[] {4}, new int[] {4}));
        check("[]", Playlists.shared(new int[] {4}, new int[] {5}));
    }

    @Test(order = 7)
    void shortListInsideALongOne() {
        check("[10, 60]", Playlists.shared(new int[] {50, 10, 40, 20, 60, 30}, new int[] {60, 10}));
    }

    @Test(order = 8, timeoutMs = 300)
    void evensAgainstOddsWithThreeShared() {
        int n = 100_000;
        int[] mine = new int[n];
        int[] yours = new int[n];
        for (int i = 0; i < n; i++) {
            mine[i] = 2 * i;
            yours[i] = 2 * i + 1;
        }
        yours[n - 3] = 0;
        yours[n - 2] = 100;
        yours[n - 1] = 2 * (n - 1); // 199,998
        check("[0, 100, 199998]", Playlists.shared(mine, yours));
    }
}
