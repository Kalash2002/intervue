import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(int[] want, int[][] zones, int[] points) {
        String input =
                zones.length + points.length <= 30
                        ? "zones = " + Arrays.deepToString(zones) + ", points = " + Arrays.toString(points)
                        : zones.length + " zones, " + points.length + " points";
        int[] got = Coverage.smallestZone(zones, points.clone());
        assertTrue(got != null, "return an array of answers, not null, for " + input);
        assertEquals(want.length, got.length, "one answer per point for " + input);
        for (int j = 0; j < want.length; j++) {
            if (want[j] != got[j]) { // the message is built only for a wrong answer
                assertEquals(want[j], got[j], "answer for point " + j + " (" + points[j] + ") for " + input);
            }
        }
    }

    @Test(order = 1)
    void sample() {
        expect(new int[] {2, 3, -1, 9}, new int[][] {{10, 18}, {14, 15}, {16, 30}, {25, 27}}, new int[] {15, 26, 9, 17});
        expect(new int[] {21, 100, 21}, new int[][] {{1, 100}, {40, 60}}, new int[] {50, 61, 50});
        expect(new int[] {1, -1}, new int[][] {{7, 7}}, new int[] {7, 8});
    }

    @Test(order = 2)
    void bothEndsOfAZoneAreCovered() {
        expect(new int[] {5, 5, -1, -1}, new int[][] {{20, 24}}, new int[] {20, 24, 19, 25});
    }

    @Test(order = 3)
    void theSizeCountsBothEnds() {
        expect(new int[] {1, 2}, new int[][] {{63, 63}, {63, 64}}, new int[] {63, 64});
    }

    @Test(order = 4)
    void answersFollowTheOrderOfThePoints() {
        expect(new int[] {20, 5, 20, 5, -1}, new int[][] {{5, 9}, {1, 20}}, new int[] {12, 6, 12, 6, 25});
    }

    @Test(order = 5)
    void theSmallestZoneHasEndedBeforeAPoint() {
        // at post 12 the small zone [10,11] is over, but the large one still covers it
        expect(new int[] {2, 36}, new int[][] {{10, 11}, {5, 40}}, new int[] {11, 12});
    }

    @Test(order = 6)
    void aZoneThatStartsLaterIsSmaller() {
        expect(new int[] {2, 50}, new int[][] {{1, 50}, {30, 31}}, new int[] {30, 32});
    }

    @Test(order = 7)
    void nestedZones() {
        expect(
                new int[] {3, 201, 801, 1000, 1000},
                new int[][] {{1, 1000}, {100, 900}, {400, 600}, {499, 501}},
                new int[] {500, 450, 150, 950, 1000});
    }

    @Test(order = 8)
    void noZoneCoversAnyPoint() {
        expect(new int[] {-1, -1, -1}, new int[][] {{10, 20}, {30, 40}}, new int[] {5, 25, 45});
    }

    @Test(order = 9)
    void postsAtTheLimits() {
        expect(new int[] {2, 1_000_000_000}, new int[][] {{1, 1_000_000_000}, {999_999_999, 1_000_000_000}}, new int[] {1_000_000_000, 1});
    }

    @Test(order = 10)
    void randomShortLists() {
        Random random = new Random(1577);
        for (int round = 0; round < 300; round++) {
            int[][] zones = new int[1 + random.nextInt(8)][];
            for (int i = 0; i < zones.length; i++) {
                int from = 40 + random.nextInt(25);
                zones[i] = new int[] {from, from + random.nextInt(9)};
            }
            int[] points = new int[1 + random.nextInt(8)];
            for (int j = 0; j < points.length; j++) {
                points[j] = 38 + random.nextInt(36);
            }
            expect(everyZone(zones, points), zones, points);
        }
    }

    /** Checks every zone for every point. */
    private static int[] everyZone(int[][] zones, int[] points) {
        int[] answer = new int[points.length];
        for (int j = 0; j < points.length; j++) {
            answer[j] = -1;
            for (int[] zone : zones) {
                int size = zone[1] - zone[0] + 1;
                if (zone[0] <= points[j] && points[j] <= zone[1] && (answer[j] == -1 || size < answer[j])) {
                    answer[j] = size;
                }
            }
        }
        return answer;
    }

    @Test(order = 11, timeoutMs = 600)
    void gapsCoveredOnlyByTheWidestZone() {
        // Small zones [10k + 1, 10k + 5] for k below 99,999, and one zone [1, 1,000,000] in the
        // middle of the list. Point j is in small zone j / 2 when j is even (size 5) and in the gap
        // after it when j is odd, where only the wide zone covers it (size 1,000,000).
        int n = 100_000;
        int[][] zones = new int[n][];
        for (int k = 0; k < n - 1; k++) {
            zones[k < 50_000 ? k : k + 1] = new int[] {10 * k + 1, 10 * k + 5};
        }
        zones[50_000] = new int[] {1, 1_000_000};
        int[] points = new int[n];
        int[] want = new int[n];
        for (int j = 0; j < n; j++) {
            points[j] = 10 * (j / 2) + (j % 2 == 0 ? 3 : 8);
            want[j] = j % 2 == 0 ? 5 : 1_000_000;
        }
        expect(want, zones, points);
    }

    @Test(order = 12, timeoutMs = 1500)
    void nestedAroundOnePost() {
        // Zone k is [100,000 - k, 100,000 + k], size 2k + 1, for k below 100,000. A point d posts
        // away from post 100,000, on either side, is covered by zones d and up, so the answer is
        // 2d + 1. The even distances come first, to the right and outwards, then the odd ones, to
        // the left and outwards: two runs, so sorting the points costs little on a cold JVM.
        int n = 100_000;
        int[][] zones = new int[n][];
        for (int k = 0; k < n; k++) {
            zones[k] = new int[] {100_000 - k, 100_000 + k};
        }
        int[] points = new int[n];
        int[] want = new int[n];
        for (int j = 0; j < n; j++) {
            int d = j < n / 2 ? 2 * j : 2 * (j - n / 2) + 1;
            points[j] = j < n / 2 ? 100_000 + d : 100_000 - d;
            want[j] = 2 * d + 1;
        }
        expect(want, zones, points);
    }
}
