import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Finds the distinct groups of three values that add up to zero. */
public class Triples {

    /** Returns each distinct zero triple once, as three numbers in non-decreasing order. */
    static List<List<Integer>> zeroSum(int[] nums) {
        int[] a = nums.clone();
        Arrays.sort(a);
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < a.length - 2 && a[i] <= 0; i++) {
            if (i > 0 && a[i] == a[i - 1]) {
                continue; // the same first value would repeat triples already found
            }
            int left = i + 1;
            int right = a.length - 1;
            while (left < right) {
                int sum = a[i] + a[left] + a[right];
                if (sum < 0) {
                    left++;
                } else if (sum > 0) {
                    right--;
                } else {
                    result.add(List.of(a[i], a[left], a[right]));
                    left++;
                    right--;
                    while (left < right && a[left] == a[left - 1]) {
                        left++; // skip repeats of the pair's left value
                    }
                }
            }
        }
        return result;
    }
}
