/** Reorders a linked list to alternate between its front and its back. */
public class ReorderList {

    /** Relinks the list in place: first, last, second, second-to-last, and so on. */
    static void reorder(ListNode head) {
        if (head == null) {
            return;
        }
        ListNode slow = head;
        ListNode fast = head;
        while (fast.next != null && fast.next.next != null) { // slow stops at the end of the first half
            slow = slow.next;
            fast = fast.next.next;
        }
        ListNode back = slow.next;
        slow.next = null; // cut, or the merged list loops back into the second half
        ListNode prev = null;
        while (back != null) { // reverse the second half so its first node is the old last
            ListNode next = back.next;
            back.next = prev;
            prev = back;
            back = next;
        }
        ListNode front = head;
        back = prev;
        while (back != null) { // the second half is never longer than the first
            ListNode frontNext = front.next;
            ListNode backNext = back.next;
            front.next = back;
            back.next = frontNext;
            front = frontNext;
            back = backNext;
        }
    }
}
