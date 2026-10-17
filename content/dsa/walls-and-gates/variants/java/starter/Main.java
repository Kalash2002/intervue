public class Main {
    public static void main(String[] args) {
        int inf = Integer.MAX_VALUE;
        int[][] rooms = {{0, inf, inf, -1}, {-1, -1, inf, inf}, {inf, inf, inf, 0}};
        FloorPlan.fillDistances(rooms);
        for (int[] row : rooms) {
            System.out.println(java.util.Arrays.toString(row));
        }
    }
}
