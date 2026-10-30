public class Main {
    public static void main(String[] args) {
        String[][] tickets = {{"VNO", "KUN"}, {"KUN", "VNO"}, {"VNO", "RIX"}, {"RIX", "VNO"}};
        System.out.println(Itinerary.route(tickets, "VNO"));
    }
}
