/** Answers "how long until it gets warmer" questions about a row of daily temperatures. */
public class Forecast {

    /** Returns, for each day, the days to wait for a strictly warmer day, or 0 if none comes. */
    static int[] daysToWarmer(int[] temps) {
        int[] answer = new int[temps.length];
        for (int day = 0; day < temps.length; day++) {
            for (int later = day + 1; later < temps.length; later++) {
                if (temps[later] > temps[day]) {
                    answer[day] = later - day;
                    break;
                }
            }
        }
        return answer;
    }
}
