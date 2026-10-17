import java.util.Arrays;

/** Checks whether every course can be taken given which courses must come before which. */
public class Curriculum {

    /** Returns true if all courses can be finished; each pair [a, b] means b must be finished before a. */
    static boolean canFinish(int numCourses, int[][] prerequisites) {
        int[] waiting = new int[numCourses]; // prerequisites each course still waits for, one per pair
        int[] first = new int[numCourses]; // the pairs whose prerequisite is course b, as a linked list per b
        int[] next = new int[prerequisites.length];
        Arrays.fill(first, -1);
        for (int i = 0; i < prerequisites.length; i++) {
            int course = prerequisites[i][0];
            int before = prerequisites[i][1];
            waiting[course]++;
            next[i] = first[before];
            first[before] = i;
        }
        int[] ready = new int[numCourses]; // courses with nothing left to wait for, in the order they freed up
        int tail = 0;
        for (int course = 0; course < numCourses; course++) {
            if (waiting[course] == 0) {
                ready[tail++] = course;
            }
        }
        for (int head = 0; head < tail; head++) {
            for (int pair = first[ready[head]]; pair != -1; pair = next[pair]) {
                int course = prerequisites[pair][0];
                if (--waiting[course] == 0) {
                    ready[tail++] = course; // its last prerequisite was just taken
                }
            }
        }
        return tail == numCourses; // a course on a loop never reaches zero
    }
}
