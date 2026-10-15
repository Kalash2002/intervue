/** Measures the rain water a row of bars can hold. */
public class Basin {

    /** Returns the total water held between the bars. */
    static int trapped(int[] heights) {
        int total = 0;
        for (int i = 0; i < heights.length; i++) {
            int leftMax = 0;
            for (int j = 0; j <= i; j++) {
                leftMax = Math.max(leftMax, heights[j]);
            }
            int rightMax = 0;
            for (int j = i; j < heights.length; j++) {
                rightMax = Math.max(rightMax, heights[j]);
            }
            total += Math.min(leftMax, rightMax) - heights[i];
        }
        return total;
    }
}
