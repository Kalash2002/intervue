/** Finds the cable that can be removed from a campus network so that it becomes a tree. */
public class Cables {

    /** Returns the spare cable: the last listed cable whose removal leaves every building connected. */
    static int[] spare(int[][] cables) {
        int n = cables.length;
        int[] parent = new int[n];
        int[] size = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
        for (int[] cable : cables) {
            int a = find(parent, cable[0]);
            int b = find(parent, cable[1]);
            if (a == b) {
                return cable;
            }
            if (size[a] < size[b]) {
                int swap = a;
                a = b;
                b = swap;
            }
            parent[b] = a;
            size[a] += size[b];
        }
        return new int[0];
    }

    private static int find(int[] parent, int x) {
        while (parent[x] != x) {
            parent[x] = parent[parent[x]];
            x = parent[x];
        }
        return x;
    }
}
