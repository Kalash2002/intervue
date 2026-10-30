/** Orders courses so that each comes after its prerequisites. */
public class StudyPlan {

    /** Returns every course once, each after its prerequisites, or an empty array if impossible; [a, b] means b before a. */
    static int[] findOrder(int numCourses, int[][] prerequisites) {
        boolean[] taken = new boolean[numCourses];
        int[] order = new int[numCourses];
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
                    order[count++] = course;
                }
            }
            if (count == before) {
                return count == numCourses ? order : new int[0]; // a pass that frees nothing: done, or stuck
            }
        }
    }
}
