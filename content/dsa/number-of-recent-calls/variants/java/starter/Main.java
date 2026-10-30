public class Main {
    public static void main(String[] args) {
        CallCounter counter = new CallCounter(10);
        for (int t : new int[] {4, 9, 14, 15, 25}) {
            System.out.println(counter.ping(t));
        }
    }
}
