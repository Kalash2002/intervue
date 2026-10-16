public class Main {
    public static void main(String[] args) {
        TimeMap map = new TimeMap();
        map.set("door", "locked", 10);
        map.set("door", "open", 20);
        map.set("door", "locked", 45);
        map.set("door", "open", 70);
        System.out.println(map.get("door", 50));
    }
}
