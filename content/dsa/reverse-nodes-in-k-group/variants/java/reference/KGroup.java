/** Reverses a linked list k nodes at a time. */
public class KGroup {

    /** Returns the head of the list with every full group of k nodes reversed, built from the same nodes. */
    static ListNode reverse(ListNode head, int k) {
        ListNode dummy = new ListNode(0, head);
        ListNode before = dummy; // the node just before the next group
        while (true) {
            ListNode last = before;
            for (int i = 0; i < k && last != null; i++) {
                last = last.next;
            }
            if (last == null) {
                return dummy.next; // fewer than k nodes left: they stay as they are
            }
            ListNode first = before.next;
            ListNode after = last.next;
            // reverse first..last; starting prev at after links the group's new end to the rest
            ListNode prev = after;
            ListNode cur = first;
            while (cur != after) {
                ListNode next = cur.next;
                cur.next = prev;
                prev = cur;
                cur = next;
            }
            before.next = last;
            before = first;
        }
    }
}
