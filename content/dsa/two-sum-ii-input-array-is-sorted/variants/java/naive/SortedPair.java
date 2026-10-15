/** Finds the pair of positions in a sorted array that adds up to a target. */
public class SortedPair {

    /** Returns {i, j} with i < j and numbers[i] + numbers[j] == target. */
    static int[] find(int[] numbers, int target) {
        for (int i = 0; i < numbers.length; i++) {
            for (int j = i + 1; j < numbers.length; j++) {
                if (numbers[i] + numbers[j] == target) {
                    return new int[] {i, j};
                }
            }
        }
        throw new IllegalStateException("the input always has a pair");
    }
}
