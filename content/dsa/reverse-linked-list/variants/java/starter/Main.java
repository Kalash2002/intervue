import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        ListNode head = ListNode.of(8, 3, 5, 1);
        System.out.println(Arrays.toString(ListNode.toArray(ReverseList.reverse(head))));
    }
}
