import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        ListNode head = ListNode.of(10, 20, 30, 40, 50, 60, 70);
        System.out.println(Arrays.toString(ListNode.toArray(KGroup.reverse(head, 3))));
    }
}
