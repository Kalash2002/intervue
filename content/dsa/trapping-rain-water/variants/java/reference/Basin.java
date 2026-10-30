/** Measures the rain water a row of bars can hold. */
public class Basin {

    /** Returns the total water held between the bars. */
    static int trapped(int[] heights) {
        int left = 0;
        int right = heights.length - 1;
        int leftMax = 0; // tallest bar seen so far from the left
        int rightMax = 0; // tallest bar seen so far from the right
        int total = 0;
        while (left < right) {
            if (heights[left] < heights[right]) {
                // a bar at least this tall stands to the right, so the left maximum decides
                leftMax = Math.max(leftMax, heights[left]);
                total += leftMax - heights[left];
                left++;
            } else {
                rightMax = Math.max(rightMax, heights[right]);
                total += rightMax - heights[right];
                right--;
            }
        }
        return total;
    }
}
