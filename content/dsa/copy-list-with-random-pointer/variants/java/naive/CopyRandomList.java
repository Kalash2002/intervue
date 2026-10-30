/** Makes a deep copy of a list whose nodes also carry a random link. */
public class CopyRandomList {

    /** Returns the head of a new list with the same values, next links and random links. */
    static Node copy(Node head) {
        Node dummy = new Node(0);
        Node tail = dummy;
        for (Node n = head; n != null; n = n.next) {
            tail.next = new Node(n.val);
            tail = tail.next;
        }
        Node twin = dummy.next;
        for (Node n = head; n != null; n = n.next, twin = twin.next) {
            if (n.random == null) {
                continue;
            }
            // walk both lists in step from the front until the original reaches the random target
            Node a = head;
            Node b = dummy.next;
            while (a != n.random) {
                a = a.next;
                b = b.next;
            }
            twin.random = b;
        }
        return dummy.next;
    }
}
