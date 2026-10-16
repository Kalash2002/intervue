import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        ListNode merged = MergeLists.merge(ListNode.of(2, 5, 9), ListNode.of(3, 4, 10, 12));
        System.out.println(Arrays.toString(ListNode.toArray(merged)));
    }
}
