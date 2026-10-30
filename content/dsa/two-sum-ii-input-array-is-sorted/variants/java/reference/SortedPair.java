/** Finds the pair of positions in a sorted array that adds up to a target. */
public class SortedPair {

    /** Returns {i, j} with i < j and numbers[i] + numbers[j] == target. */
    static int[] find(int[] numbers, int target) {
        int left = 0;
        int right = numbers.length - 1;
        while (left < right) {
            int sum = numbers[left] + numbers[right];
            if (sum == target) {
                return new int[] {left, right};
            } else if (sum > target) {
                right--; // the right value is too big to pair with any remaining left value
            } else {
                left++; // the left value is too small to pair with any remaining right value
            }
        }
        throw new IllegalStateException("the input always has a pair");
    }
}
