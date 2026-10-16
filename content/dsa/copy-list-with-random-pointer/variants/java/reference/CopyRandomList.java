/** Makes a deep copy of a list whose nodes also carry a random link. */
public class CopyRandomList {

    /** Returns the head of a new list with the same values, next links and random links. */
    static Node copy(Node head) {
        // place each copy right after its original: A, A', B, B', ...
        for (Node n = head; n != null; n = n.next.next) {
            Node twin = new Node(n.val);
            twin.next = n.next;
            n.next = twin;
        }
        // the copy of n.random is the node right after it
        for (Node n = head; n != null; n = n.next.next) {
            n.next.random = n.random == null ? null : n.random.next;
        }
        // unweave: restore the input and chain the copies
        Node dummy = new Node(0);
        Node tail = dummy;
        for (Node n = head; n != null; n = n.next) {
            tail.next = n.next;
            tail = tail.next;
            n.next = tail.next;
        }
        return dummy.next;
    }
}
