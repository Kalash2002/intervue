/** Adds two numbers stored as linked lists of digits, least significant digit first. */
public class AddNumbers {

    /** Returns the sum as a new list of digits, least significant digit first. */
    static ListNode add(ListNode first, ListNode second) {
        ListNode dummy = new ListNode();
        ListNode tail = dummy;
        int carry = 0;
        // a carry left after both lists end still needs a digit of its own
        while (first != null || second != null || carry != 0) {
            int sum = carry;
            if (first != null) {
                sum += first.val;
                first = first.next;
            }
            if (second != null) {
                sum += second.val;
                second = second.next;
            }
            tail.next = new ListNode(sum % 10);
            tail = tail.next;
            carry = sum / 10;
        }
        return dummy.next;
    }
}
