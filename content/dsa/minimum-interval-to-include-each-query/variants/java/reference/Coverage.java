import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/** Finds which radio zone serves a phone at a post. */
public class Coverage {

    /** Returns, for each point, the size of the smallest zone [from, to] covering it, or -1. */
    static int[] smallestZone(int[][] zones, int[] points) {
        int[][] byFrom = zones.clone();
        Arrays.sort(byFrom, Comparator.comparingInt(z -> z[0]));
        Integer[] order = new Integer[points.length];
        for (int j = 0; j < order.length; j++) {
            order[j] = j;
        }
        Arrays.sort(order, Comparator.comparingInt(j -> points[j])); // answer the points from the lowest up
        PriorityQueue<int[]> open = new PriorityQueue<>(Comparator.comparingInt(z -> z[1] - z[0])); // smallest on top
        int[] answer = new int[points.length];
        int next = 0; // the next zone, by from, not yet in the heap
        for (int j : order) {
            int point = points[j];
            while (next < byFrom.length && byFrom[next][0] <= point) {
                open.add(byFrom[next++]);
            }
            while (!open.isEmpty() && open.peek()[1] < point) {
                open.poll(); // ended before this point, so before every later point too
            }
            answer[j] = open.isEmpty() ? -1 : open.peek()[1] - open.peek()[0] + 1;
        }
        return answer;
    }
}
