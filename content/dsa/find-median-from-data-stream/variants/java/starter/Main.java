public class Main {
    public static void main(String[] args) {
        MedianFinder finder = new MedianFinder();
        finder.addNum(6);
        System.out.println(finder.findMedian());
        finder.addNum(-2);
        System.out.println(finder.findMedian());
    }
}
