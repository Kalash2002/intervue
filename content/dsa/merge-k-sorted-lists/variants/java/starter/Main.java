import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        ListNode[] lists = {ListNode.of(4, 7, 12), ListNode.of(1, 9), ListNode.of(5, 6, 15)};
        System.out.println(Arrays.toString(ListNode.toArray(MergeK.merge(lists))));
    }
}
