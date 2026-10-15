/** Answers "how long until it gets warmer" questions about a row of daily temperatures. */
public class Forecast {

    /** Returns, for each day, the days to wait for a strictly warmer day, or 0 if none comes. */
    static int[] daysToWarmer(int[] temps) {
        int[] answer = new int[temps.length]; // zero means no warmer day follows
        int[] waiting = new int[temps.length]; // positions of days still waiting
        int size = 0;
        for (int day = 0; day < temps.length; day++) {
            while (size > 0 && temps[waiting[size - 1]] < temps[day]) { // strictly colder
                int earlier = waiting[--size];
                answer[earlier] = day - earlier;
            }
            waiting[size++] = day;
        }
        return answer;
    }
}
