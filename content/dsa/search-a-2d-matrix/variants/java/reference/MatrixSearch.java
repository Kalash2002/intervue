/** Lookups in a grid whose values increase row by row. */
public class MatrixSearch {

    static boolean contains(int[][] matrix, int target) {
        int columns = matrix[0].length;
        int low = 0;
        int high = matrix.length * columns - 1; // flat positions, read row by row
        while (low <= high) {
            int middle = (low + high) >>> 1;
            int value = matrix[middle / columns][middle % columns]; // divide by the row length, not the row count
            if (value == target) {
                return true;
            }
            if (value < target) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }
        return false;
    }
}
