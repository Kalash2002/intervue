/** Plans which houses along a street to empty. */
public class Street {

    /** Returns the largest total of cash from houses no two of which are neighbours. */
    static int maxLoot(int[] cash) {
        int twoBack = 0; // best total for the street up to two houses before this one
        int oneBack = 0; // best total for the street up to the house before this one
        for (int amount : cash) {
            int here = Math.max(oneBack, twoBack + amount); // leave this house, or take it and skip its neighbour
            twoBack = oneBack;
            oneBack = here;
        }
        return oneBack;
    }
}
