/** Smashes stones together, heaviest two at a time, until at most one is left. */
public class LastStone {

    /** Returns the weight of the last stone left, or 0 if none is left. */
    static int lastWeight(int[] stones) {
        int[] left = stones.clone();
        int size = left.length;
        while (size > 1) {
            int first = 0; // position of the heaviest
            for (int i = 1; i < size; i++) {
                if (left[i] > left[first]) {
                    first = i;
                }
            }
            int heavier = left[first];
            left[first] = left[--size]; // remove it by moving the last stone into its place
            int second = 0;
            for (int i = 1; i < size; i++) {
                if (left[i] > left[second]) {
                    second = i;
                }
            }
            int lighter = left[second];
            if (heavier > lighter) {
                left[second] = heavier - lighter;
            } else {
                left[second] = left[--size];
            }
        }
        return size == 0 ? 0 : left[0];
    }
}
