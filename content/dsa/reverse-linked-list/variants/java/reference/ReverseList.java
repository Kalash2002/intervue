/** Reverses a singly linked list in place. */
public class ReverseList {

    /** Returns the head of the reversed list, built from the same nodes. */
    static ListNode reverse(ListNode head) {
        ListNode prev = null; // the reversed part so far
        ListNode cur = head;
        while (cur != null) {
            ListNode next = cur.next; // save it before the link is overwritten
            cur.next = prev;
            prev = cur;
            cur = next;
        }
        return prev;
    }
}
