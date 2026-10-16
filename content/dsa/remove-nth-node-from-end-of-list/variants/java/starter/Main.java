import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        ListNode head = RemoveFromEnd.remove(ListNode.of(6, 11, 2, 19, 30), 2);
        System.out.println(Arrays.toString(ListNode.toArray(head)));
    }
}
