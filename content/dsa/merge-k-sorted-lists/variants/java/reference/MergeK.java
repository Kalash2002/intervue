import java.util.PriorityQueue;

/** Merges k sorted linked lists into one sorted list. */
public class MergeK {

    /** Returns the head of one non-decreasing list made of every node of the given lists. */
    static ListNode merge(ListNode[] lists) {
        // the heap holds the front node of every list that still has nodes
        PriorityQueue<ListNode> fronts = new PriorityQueue<>((a, b) -> Integer.compare(a.val, b.val));
        for (ListNode head : lists) {
            if (head != null) { // empty lists have no front to compare
                fronts.add(head);
            }
        }
        ListNode dummy = new ListNode();
        ListNode tail = dummy;
        while (!fronts.isEmpty()) {
            ListNode smallest = fronts.poll();
            tail.next = smallest;
            tail = smallest;
            if (smallest.next != null) {
                fronts.add(smallest.next);
            }
        }
        return dummy.next;
    }
}
