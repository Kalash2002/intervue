public class Main {
    public static void main(String[] args) {
        LRUCache cache = new LRUCache(2);
        cache.put(5, 50);
        cache.put(8, 80);
        System.out.println(cache.get(8) + " " + cache.get(5));
        cache.put(3, 30);
        System.out.println(cache.get(8));
    }
}
