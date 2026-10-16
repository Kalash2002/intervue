/** Merges two sorted linked lists. */
public class MergeLists {

    /** Returns the head of one sorted list made of the nodes of both lists. */
    static ListNode merge(ListNode first, ListNode second) {
        ListNode dummy = new ListNode(); // a fixed node before the head, so the first append is not special
        ListNode tail = dummy;
        while (first != null && second != null) {
            if (first.val <= second.val) { // <= takes ties from the first list: stable
                tail.next = first;
                first = first.next;
            } else {
                tail.next = second;
                second = second.next;
            }
            tail = tail.next;
        }
        tail.next = first != null ? first : second; // the rest of one list is already sorted
        return dummy.next;
    }
}
