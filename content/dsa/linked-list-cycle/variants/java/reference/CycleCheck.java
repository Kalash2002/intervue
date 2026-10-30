/** Detects whether a linked list loops back on itself. */
public class CycleCheck {

    /** Returns true when following next from head reaches a node it has already visited. */
    static boolean hasCycle(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;
        while (fast != null && fast.next != null) { // fast reaches the end first if there is one
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) { // compare nodes, not values: values may repeat
                return true;
            }
        }
        return false;
    }
}
