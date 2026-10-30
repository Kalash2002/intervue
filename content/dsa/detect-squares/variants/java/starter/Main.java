public class Main {
    public static void main(String[] args) {
        SquareFinder finder = new SquareFinder();
        finder.add(6, 7);
        finder.add(10, 7);
        finder.add(10, 3);
        finder.add(2, 7);
        finder.add(2, 3);
        System.out.println(finder.count(6, 3) + " " + finder.count(6, 7));
    }
}
