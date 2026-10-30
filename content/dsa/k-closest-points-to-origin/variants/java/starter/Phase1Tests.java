import static kit.Check.assertEquals;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static final Comparator<int[]> BY_X_THEN_Y =
            Comparator.<int[]>comparingInt(p -> p[0]).thenComparingInt(p -> p[1]);

    /** Compares the answer with the expected points, ignoring order. */
    private static void expect(int[][] expected, int[][] got) {
        assertEquals(expected.length, got.length, "number of points returned");
        int[][] a = expected.clone();
        int[][] b = got.clone();
        Arrays.sort(a, BY_X_THEN_Y);
        Arrays.sort(b, BY_X_THEN_Y);
        for (int i = 0; i < a.length; i++) {
            if (b[i] == null || b[i].length != 2 || a[i][0] != b[i][0] || a[i][1] != b[i][1]) {
                assertEquals(Arrays.toString(a[i]), Arrays.toString(b[i]), "point " + i + " in sorted order");
            }
        }
    }

    @Test(order = 1)
    void sample() {
        int[][] points = {{3, 4}, {-1, 2}, {6, -1}, {0, -3}};
        expect(new int[][] {{-1, 2}, {0, -3}}, ClosestPoints.kClosest(points, 2));
    }

    @Test(order = 2)
    void tiesInsideTheAnswer() {
        int[][] points = {{2, 2}, {-2, -2}, {1, -3}, {5, 0}};
        expect(new int[][] {{2, 2}, {-2, -2}, {1, -3}}, ClosestPoints.kClosest(points, 3));
    }

    @Test(order = 3)
    void squaresPastTheIntRange() {
        int[][] points = {{60_000, 0}, {30_000, -30_000}};
        expect(new int[][] {{30_000, -30_000}}, ClosestPoints.kClosest(points, 1));
        int[][] far = {{-100_000, 100_000}, {99_999, -100_000}, {50_000, 50_000}};
        expect(new int[][] {{50_000, 50_000}, {99_999, -100_000}}, ClosestPoints.kClosest(far, 2));
    }

    @Test(order = 4)
    void kEqualsTheNumberOfPoints() {
        int[][] points = {{7, -7}, {0, 0}, {-3, 8}};
        expect(new int[][] {{7, -7}, {0, 0}, {-3, 8}}, ClosestPoints.kClosest(points, 3));
    }

    @Test(order = 5)
    void singlePoint() {
        expect(new int[][] {{-4, 9}}, ClosestPoints.kClosest(new int[][] {{-4, 9}}, 1));
    }

    @Test(order = 6)
    void repeatedPoints() {
        int[][] points = {{1, 1}, {5, 5}, {1, 1}, {-2, 0}, {5, 5}};
        expect(new int[][] {{1, 1}, {1, 1}, {-2, 0}}, ClosestPoints.kClosest(points, 3));
    }

    @Test(order = 7)
    void originAndAxes() {
        int[][] points = {{0, 9}, {-8, 0}, {0, 0}, {6, 6}, {0, -5}};
        expect(new int[][] {{0, 0}, {0, -5}}, ClosestPoints.kClosest(points, 2));
    }

    @Test(order = 8, timeoutMs = 1000)
    void hundredThousandPoints() {
        int n = 100_000;
        int[][] points = new int[n][];
        Random random = new Random(21);
        for (int i = 1; i <= n; i++) {
            // squared distance i^2 + (i - 1)^2 grows with i, so the k closest are i = 1..k
            int a = random.nextBoolean() ? i : -i;
            int b = random.nextBoolean() ? i - 1 : 1 - i;
            points[i - 1] = random.nextBoolean() ? new int[] {a, b} : new int[] {b, a};
        }
        int[][] byDistance = points.clone();
        for (int i = n - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int[] swap = points[i];
            points[i] = points[j];
            points[j] = swap;
        }
        for (int k : new int[] {1, 777, 50_000, 99_999, 100_000}) {
            int[][] got = ClosestPoints.kClosest(points, k);
            assertEquals(k, got.length, "number of points returned");
            boolean[] seen = new boolean[n + 1];
            for (int[] p : got) {
                int i = Math.max(Math.abs(p[0]), Math.abs(p[1])); // which point this is
                boolean ok = i >= 1 && i <= k && !seen[i] && Arrays.equals(p, byDistance[i - 1]);
                if (!ok) {
                    assertEquals(true, ok, "point " + Arrays.toString(p) + " is not one of the " + k + " closest");
                }
                seen[i] = true;
            }
        }
    }
}
