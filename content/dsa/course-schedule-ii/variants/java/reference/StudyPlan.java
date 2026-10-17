import java.util.Arrays;

/** Orders courses so that each comes after its prerequisites. */
public class StudyPlan {

    /** Returns every course once, each after its prerequisites, or an empty array if impossible; [a, b] means b before a. */
    static int[] findOrder(int numCourses, int[][] prerequisites) {
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
        int[] order = new int[numCourses]; // doubles as the queue: courses are taken in the order they free up
        int tail = 0;
        for (int course = 0; course < numCourses; course++) {
            if (waiting[course] == 0) {
                order[tail++] = course;
            }
        }
        for (int head = 0; head < tail; head++) {
            for (int pair = first[order[head]]; pair != -1; pair = next[pair]) {
                int course = prerequisites[pair][0];
                if (--waiting[course] == 0) {
                    order[tail++] = course; // its last prerequisite was just taken
                }
            }
        }
        return tail == numCourses ? order : new int[0]; // courses on a loop never free up
    }
}
