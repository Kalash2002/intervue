import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        ListNode sum = AddNumbers.add(ListNode.of(7, 3, 2), ListNode.of(5, 9));
        System.out.println(Arrays.toString(ListNode.toArray(sum)));
    }
}
