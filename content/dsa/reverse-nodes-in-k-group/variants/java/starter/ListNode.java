/** A node of a singly linked list. Read-only: the tests build and read lists with the helpers. */
public class ListNode {
    int val;
    ListNode next;

    ListNode() {}

    ListNode(int val) {
        this.val = val;
    }

    ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }

    /** Builds a list holding the values in order; no values gives null. */
    static ListNode of(int... values) {
        return withCycle(values, -1);
    }

    /** Builds a list whose last node links back to the node at index pos; pos -1 means no link. */
    static ListNode withCycle(int[] values, int pos) {
        ListNode dummy = new ListNode();
        ListNode tail = dummy;
        ListNode target = null;
        for (int i = 0; i < values.length; i++) {
            tail.next = new ListNode(values[i]);
            tail = tail.next;
            if (i == pos) {
                target = tail;
            }
        }
        tail.next = target;
        return dummy.next;
    }

    /** Reads the values from head to the end; fails on a list that loops back on itself. */
    static int[] toArray(ListNode head) {
        int count = 0;
        for (ListNode n = head; n != null; n = n.next) {
            if (++count > 1_000_000) { // longer than any test list: it must loop
                throw new IllegalStateException("the list does not end: some node links back");
            }
        }
        int[] values = new int[count];
        int i = 0;
        for (ListNode n = head; n != null; n = n.next) {
            values[i++] = n.val;
        }
        return values;
    }
}
