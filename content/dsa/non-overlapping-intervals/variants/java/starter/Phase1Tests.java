import static kit.Check.assertEquals;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(int want, int[][] requests) {
        String input = requests.length <= 20 ? "requests = " + Arrays.deepToString(requests) : requests.length + " requests";
        assertEquals(want, Telescope.minCancellations(requests), input);
    }

    @Test(order = 1)
    void sample() {
        expect(2, new int[][] {{9, 15}, {1, 4}, {3, 8}, {12, 13}, {4, 7}, {14, 20}});
        expect(1, new int[][] {{5, 16}, {15, 19}, {18, 22}});
        expect(1, new int[][] {{7, 19}, {7, 19}});
    }

    @Test(order = 2)
    void touchingRequestsDoNotOverlap() {
        expect(0, new int[][] {{5, 8}, {8, 12}, {12, 13}});
        expect(0, new int[][] {{12, 13}, {5, 8}, {8, 12}});
    }

    @Test(order = 3)
    void keepingTheFirstToStartIsWrong() {
        // [2,14] starts first but blocks three short requests that fit together
        expect(1, new int[][] {{2, 14}, {3, 6}, {7, 10}, {11, 13}});
        expect(1, new int[][] {{10, 40}, {12, 14}, {15, 18}, {20, 21}});
    }

    @Test(order = 4)
    void cancellingTheLongestIsWrong() {
        // the longest, [100,200], can stay; cancelling it first still leaves a chain of three to break
        expect(2, new int[][] {{100, 200}, {190, 205}, {203, 210}, {208, 300}});
    }

    @Test(order = 5)
    void keepingTheShortestIsWrong() {
        // the shortest, [4,7], overlaps both of the others, which fit together
        expect(1, new int[][] {{0, 5}, {4, 7}, {6, 11}});
    }

    @Test(order = 6)
    void oneRequest() {
        expect(0, new int[][] {{40, 41}});
        expect(0, new int[][] {{0, 1_000_000_000}});
    }

    @Test(order = 7)
    void identicalRequests() {
        expect(4, new int[][] {{600, 700}, {600, 700}, {600, 700}, {600, 700}, {600, 700}});
    }

    @Test(order = 8)
    void oneLongRequestOverTouchingOnes() {
        expect(1, new int[][] {{30, 60}, {30, 45}, {45, 60}});
    }

    @Test(order = 9)
    void sameEndDifferentStarts() {
        expect(2, new int[][] {{10, 20}, {15, 20}, {5, 20}});
    }

    @Test(order = 10)
    void randomShortLists() {
        Random random = new Random(1532);
        for (int round = 0; round < 300; round++) {
            int n = 1 + random.nextInt(10);
            int[][] requests = new int[n][];
            for (int i = 0; i < n; i++) {
                int start = 50 + random.nextInt(20);
                requests[i] = new int[] {start, start + 1 + random.nextInt(8)};
            }
            expect(n - mostKept(requests), requests);
        }
    }

    /** Tries every set of requests and returns the size of the largest one with no overlap. */
    private static int mostKept(int[][] requests) {
        int n = requests.length;
        int most = 0;
        for (int mask = 1; mask < 1 << n; mask++) {
            boolean apart = true;
            for (int i = 0; i < n && apart; i++) {
                for (int j = i + 1; j < n && apart; j++) {
                    if ((mask >> i & 1) == 1 && (mask >> j & 1) == 1) {
                        apart = requests[i][1] <= requests[j][0] || requests[j][1] <= requests[i][0];
                    }
                }
            }
            if (apart) {
                most = Math.max(most, Integer.bitCount(mask));
            }
        }
        return most;
    }

    @Test(order = 11, timeoutMs = 400)
    void eachRequestOverlapsHalfTheOthers() {
        // Request i is [i, i + 50,000) for i below 100,000, listed from the last to the first.
        // Two can stay, [0, 50,000) and [50,000, 100,000), which touch; no three fit, so 99,998
        // are cancelled.
        int n = 100_000;
        int[][] requests = new int[n][];
        for (int i = 0; i < n; i++) {
            requests[n - 1 - i] = new int[] {i, i + 50_000};
        }
        expect(99_998, requests);
    }

    @Test(order = 12, timeoutMs = 400)
    void eachRequestStartsHalfWayToItsEnd() {
        // Request i is [i / 2, i + 1), the odd ones listed first, then the even ones. Keeping the
        // one that ends first each time keeps requests 0, 2, 6, 14, ..., 65,534, each two less
        // than a power of two: 16 of them.
        int n = 100_000;
        int[][] requests = new int[n][];
        int next = 0;
        for (int i = 1; i < n; i += 2) {
            requests[next++] = new int[] {i / 2, i + 1};
        }
        for (int i = 0; i < n; i += 2) {
            requests[next++] = new int[] {i / 2, i + 1};
        }
        expect(99_984, requests);
    }
}
