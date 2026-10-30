import java.util.*;

/** Finds the one ticket number of 0 to n that is missing from the n numbers left. */
public class LostTicket {

    /** Returns the number of 0 to nums.length that is not in nums. */
    static int find(int[] nums) {
        int missing = nums.length; // n is in the range 0..n but is no index of the array
        for (int i = 0; i < nums.length; i++) {
            missing ^= i ^ nums[i]; // every present number meets its copy and cancels
        }
        return missing;
    }
}
