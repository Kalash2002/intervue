/** Merges k sorted linked lists into one sorted list. */
public class MergeK {

    /** Returns the head of one non-decreasing list made of every node of the given lists. */
    static ListNode merge(ListNode[] lists) {
        ListNode[] fronts = lists.clone(); // the caller's array stays as it was
        ListNode dummy = new ListNode();
        ListNode tail = dummy;
        while (true) {
            int best = -1;
            for (int i = 0; i < fronts.length; i++) { // scan every front for the smallest
                if (fronts[i] != null && (best < 0 || fronts[i].val < fronts[best].val)) {
                    best = i;
                }
            }
            if (best < 0) {
                return dummy.next; // every list is used up
            }
            tail.next = fronts[best];
            tail = tail.next;
            fronts[best] = fronts[best].next;
        }
    }
}
