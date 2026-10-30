import java.util.Collections;
import java.util.PriorityQueue;

/** Smashes stones together, heaviest two at a time, until at most one is left. */
public class LastStone {

    /** Returns the weight of the last stone left, or 0 if none is left. */
    static int lastWeight(int[] stones) {
        PriorityQueue<Integer> heap = new PriorityQueue<>(Collections.reverseOrder()); // heaviest on top
        for (int stone : stones) {
            heap.add(stone);
        }
        while (heap.size() > 1) {
            int heavier = heap.poll();
            int lighter = heap.poll();
            if (heavier > lighter) {
                heap.add(heavier - lighter); // equal stones both disappear
            }
        }
        return heap.isEmpty() ? 0 : heap.peek();
    }
}
