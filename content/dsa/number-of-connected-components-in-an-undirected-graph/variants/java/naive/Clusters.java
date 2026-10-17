/** Counts the separate networks formed by machines and the cables between them. */
public class Clusters {

    /** Returns how many separate networks the n machines form; cables run both ways. */
    static int count(int n, int[][] links) {
        int[] label = new int[n];
        for (int i = 0; i < n; i++) {
            label[i] = i;
        }
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int[] link : links) {
                int a = label[link[0]];
                int b = label[link[1]];
                if (a != b) {
                    int low = Math.min(a, b);
                    label[link[0]] = low;
                    label[link[1]] = low;
                    changed = true;
                }
            }
        }
        int networks = 0;
        for (int i = 0; i < n; i++) {
            if (label[i] == i) {
                networks++;
            }
        }
        return networks;
    }
}
