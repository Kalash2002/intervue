import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Node head = Node.of(new Integer[][] {{6, 2}, {1, null}, {9, 0}, {4, 3}});
        Node copy = CopyRandomList.copy(head);
        System.out.println(Arrays.deepToString(Node.toPairs(copy)));
        System.out.println(Node.deepCopyProblem(head, copy) == null ? "deep copy" : Node.deepCopyProblem(head, copy));
    }
}
