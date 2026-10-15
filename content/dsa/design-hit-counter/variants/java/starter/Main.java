public class Main {
    public static void main(String[] args) {
        Pings pings = new Pings(5);
        pings.ping(1);
        pings.ping(2);
        pings.ping(4);
        System.out.println(pings.recent(4));
    }
}
