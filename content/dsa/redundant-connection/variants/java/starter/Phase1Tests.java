import static kit.Check.assertEquals;

import java.util.Arrays;
import kit.Test;

public class Phase1Tests {

    private static void expect(String want, int[][] cables) {
        assertEquals(want, Arrays.toString(Cables.spare(cables)));
    }

    @Test(order = 1)
    void sample() {
        expect("[1, 0]", new int[][] {{3, 4}, {0, 3}, {4, 1}, {1, 0}, {2, 1}});
        expect("[4, 2]", new int[][] {{0, 1}, {1, 2}, {1, 3}, {3, 4}, {4, 2}, {0, 5}});
    }

    @Test(order = 2)
    void threeBuildingsInARing() {
        expect("[2, 0]", new int[][] {{0, 1}, {1, 2}, {2, 0}});
        expect("[0, 2]", new int[][] {{1, 0}, {2, 1}, {0, 2}});
    }

    @Test(order = 3)
    void theLoopIsFarFromBuildingZero() {
        expect("[5, 3]", new int[][] {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}, {5, 3}});
    }

    @Test(order = 4)
    void loopCablesScatteredThroughTheList() {
        // the loop is 1, 2, 3, 4; its cables sit at positions 0, 2, 3 and 5, with others between
        expect("[4, 1]", new int[][] {{2, 3}, {0, 1}, {3, 4}, {1, 2}, {5, 0}, {4, 1}, {6, 5}});
    }

    @Test(order = 5)
    void aBridgeBetweenTwoBusyBuildingsIsNotSpare() {
        // cable [2, 3] is listed last and both its ends have three cables, but removing it cuts off 3, 4 and 5
        expect("[2, 0]", new int[][] {{0, 1}, {1, 2}, {2, 0}, {3, 4}, {3, 5}, {2, 3}});
    }

    @Test(order = 6, timeoutMs = 1000)
    void aHundredThousandBuildingsInOneLongLoop() {
        int n = 100_000;
        int[][] cables = new int[n][];
        for (int i = 0; i < n - 1; i++) {
            cables[i] = new int[] {i, i + 1};
        }
        cables[n - 1] = new int[] {n - 1, 0}; // closes a loop through every building
        expect("[99999, 0]", cables);
    }

    @Test(order = 7, timeoutMs = 600)
    void aSmallLoopListedFirstThenALongLine() {
        int n = 20_000;
        int[][] cables = new int[n][];
        cables[0] = new int[] {0, 1};
        cables[1] = new int[] {1, 2};
        cables[2] = new int[] {2, 0};
        for (int i = 3; i < n; i++) {
            cables[i] = new int[] {i - 1, i}; // every later cable is needed to reach building i
        }
        expect("[2, 0]", cables);
    }
}
