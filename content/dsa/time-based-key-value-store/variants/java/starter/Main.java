public class Main {
    public static void main(String[] args) {
        TimeMap map = new TimeMap();
        map.set("door", "locked", 10);
        map.set("door", "open", 20);
        System.out.println(map.get("door", 15));
    }
}
