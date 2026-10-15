/** Answers whether two stations of a tram network are joined by connectors. */
public class Routes {

    /** Returns true if station to can be reached from station from; connectors run both ways. */
    static boolean connected(int n, int[][] links, int from, int to) {
        boolean[] reached = new boolean[n];
        reached[from] = true;
        boolean grew = true;
        while (grew && !reached[to]) {
            grew = false;
            for (int[] link : links) {
                if (reached[link[0]] != reached[link[1]]) {
                    reached[link[0]] = true;
                    reached[link[1]] = true;
                    grew = true;
                }
            }
        }
        return reached[to];
    }
}
