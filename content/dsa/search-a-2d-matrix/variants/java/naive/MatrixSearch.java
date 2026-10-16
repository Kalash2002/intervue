/** Lookups in a grid whose values increase row by row. */
public class MatrixSearch {

    static boolean contains(int[][] matrix, int target) {
        for (int[] row : matrix) {
            for (int value : row) {
                if (value == target) {
                    return true;
                }
            }
        }
        return false;
    }
}
