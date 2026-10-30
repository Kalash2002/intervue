/** Reorders a linked list to alternate between its front and its back. */
public class ReorderList {

    /** Relinks the list in place: first, last, second, second-to-last, and so on. */
    static void reorder(ListNode head) {
        ListNode cur = head;
        while (cur != null && cur.next != null && cur.next.next != null) {
            ListNode beforeLast = cur;
            while (beforeLast.next.next != null) { // walk to the second-to-last node every time
                beforeLast = beforeLast.next;
            }
            ListNode last = beforeLast.next;
            beforeLast.next = null;
            last.next = cur.next;
            cur.next = last;
            cur = last.next;
        }
    }
}
