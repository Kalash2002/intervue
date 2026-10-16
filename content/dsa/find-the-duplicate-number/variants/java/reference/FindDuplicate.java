/** Finds the value that appears more than once in an array of n + 1 values from 1 to n. */
public class FindDuplicate {

    /** Returns the repeated value without changing nums. */
    static int find(int[] nums) {
        // read nums as links i -> nums[i]: no value is 0, so the walk from 0 runs into a loop
        int slow = 0;
        int fast = 0;
        do {
            slow = nums[slow];
            fast = nums[nums[fast]];
        } while (slow != fast);
        // the loop is entered at the value two indexes point to: the duplicate
        int finder = 0;
        while (finder != slow) {
            finder = nums[finder];
            slow = nums[slow];
        }
        return slow;
    }
}
