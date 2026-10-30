/** Answers "how long until it gets warmer" questions about a row of daily temperatures. */
public class Forecast {

    /** Returns, for each day, the days to wait for a strictly warmer day, or 0 if none comes. */
    static int[] daysToWarmer(int[] temps) {
        int n = temps.length;
        int[] answer = new int[n]; // zero means no warmer day follows
        for (int day = n - 2; day >= 0; day--) { // the last day always keeps 0
            int next = day + 1;
            while (temps[next] <= temps[day] && answer[next] != 0) {
                next += answer[next]; // skip to the first day warmer than the one we stand on
            }
            if (temps[next] > temps[day]) {
                answer[day] = next - day;
            }
        }
        return answer;
    }
}
