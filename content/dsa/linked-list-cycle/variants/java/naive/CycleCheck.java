/** Detects whether a linked list loops back on itself. */
public class CycleCheck {

    /** Returns true when following next from head reaches a node it has already visited. */
    static boolean hasCycle(ListNode head) {
        int visited = 0;
        for (ListNode cur = head; cur != null; cur = cur.next) {
            visited++;
            ListNode probe = head;
            for (int i = 0; i < visited; i++) { // every node visited so far, cur included
                if (probe == cur.next) {
                    return true; // cur links back to a node already walked
                }
                probe = probe.next;
            }
        }
        return false;
    }
}
