/** Counts the separate networks formed by machines and the cables between them. */
public class Clusters {

    /** Returns how many separate networks the n machines form; cables run both ways. */
    static int count(int n, int[][] links) {
        int[] parent = new int[n];
        int[] size = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
        int networks = n;
        for (int[] link : links) {
            int a = find(parent, link[0]);
            int b = find(parent, link[1]);
            if (a == b) {
                continue;
            }
            if (size[a] < size[b]) {
                int swap = a;
                a = b;
                b = swap;
            }
            parent[b] = a;
            size[a] += size[b];
            networks--;
        }
        return networks;
    }

    private static int find(int[] parent, int x) {
        while (parent[x] != x) {
            parent[x] = parent[parent[x]];
            x = parent[x];
        }
        return x;
    }
}
