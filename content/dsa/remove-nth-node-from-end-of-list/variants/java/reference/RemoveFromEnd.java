/** Removes the node a given number of places from the end of a linked list. */
public class RemoveFromEnd {

    /** Unlinks the n-th node counting from the end (n = 1 is the last) and returns the head. */
    static ListNode remove(ListNode head, int n) {
        ListNode dummy = new ListNode(0, head); // removing the head then works like any other node
        ListNode lead = dummy;
        for (int i = 0; i < n; i++) {
            lead = lead.next; // lead runs n nodes ahead of trail
        }
        ListNode trail = dummy;
        while (lead.next != null) {
            lead = lead.next;
            trail = trail.next;
        }
        trail.next = trail.next.next; // trail stops just before the node to remove
        return dummy.next;
    }
}
