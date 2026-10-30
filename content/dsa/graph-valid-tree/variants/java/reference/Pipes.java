/** Decides whether a set of pipes joins valves into a single tree. */
public class Pipes {

    /** Returns true if the pipes connect all n valves with no loop. */
    static boolean isTree(int n, int[][] links) {
        if (links.length != n - 1) {
            return false;
        }
        int[] parent = new int[n];
        int[] size = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
        for (int[] link : links) {
            int a = find(parent, link[0]);
            int b = find(parent, link[1]);
            if (a == b) {
                return false;
            }
            if (size[a] < size[b]) {
                int swap = a;
                a = b;
                b = swap;
            }
            parent[b] = a;
            size[a] += size[b];
        }
        return true;
    }

    private static int find(int[] parent, int x) {
        while (parent[x] != x) {
            parent[x] = parent[parent[x]];
            x = parent[x];
        }
        return x;
    }
}
