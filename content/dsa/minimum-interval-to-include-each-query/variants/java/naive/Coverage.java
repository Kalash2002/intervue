/** Finds which radio zone serves a phone at a post. */
public class Coverage {

    /** Returns, for each point, the size of the smallest zone [from, to] covering it, or -1. */
    static int[] smallestZone(int[][] zones, int[] points) {
        int[] answer = new int[points.length];
        for (int j = 0; j < points.length; j++) {
            int best = -1;
            for (int[] zone : zones) {
                if (zone[0] <= points[j] && points[j] <= zone[1]) { // both ends are covered
                    int size = zone[1] - zone[0] + 1;
                    if (best == -1 || size < best) {
                        best = size;
                    }
                }
            }
            answer[j] = best;
        }
        return answer;
    }
}
