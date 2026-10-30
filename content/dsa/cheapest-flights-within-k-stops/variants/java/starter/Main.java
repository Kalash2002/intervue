public class Main {
    public static void main(String[] args) {
        int[][] flights = {{0, 1, 100}, {1, 2, 100}, {2, 3, 100}, {0, 2, 250}, {1, 3, 300}};
        System.out.println(Fares.cheapest(4, flights, 0, 3, 1));
    }
}
