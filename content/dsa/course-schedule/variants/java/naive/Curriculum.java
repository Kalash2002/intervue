/** Checks whether every course can be taken given which courses must come before which. */
public class Curriculum {

    /** Returns true if all courses can be finished; each pair [a, b] means b must be finished before a. */
    static boolean canFinish(int numCourses, int[][] prerequisites) {
        boolean[] taken = new boolean[numCourses];
        int count = 0;
        while (true) {
            boolean[] blocked = new boolean[numCourses];
            for (int[] pair : prerequisites) {
                if (!taken[pair[1]]) {
                    blocked[pair[0]] = true; // still waiting for a course not taken yet
                }
            }
            int before = count;
            for (int course = 0; course < numCourses; course++) {
                if (!taken[course] && !blocked[course]) {
                    taken[course] = true;
                    count++;
                }
            }
            if (count == before) {
                return count == numCourses; // a pass that frees nothing: done, or stuck on a loop
            }
        }
    }
}
