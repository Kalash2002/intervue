/** Hands arriving jobs to machines by weight. Scans every machine for every job. */
public class Dispatcher {

    static int[] assign(int[] weights, int[] durations) {
        int machines = weights.length;
        long[] freeAt = new long[machines];
        int[] answer = new int[durations.length];
        long now = 0;
        for (int job = 0; job < durations.length; job++) {
            now = Math.max(now, job);
            int best = lightestFree(weights, freeAt, now);
            if (best < 0) {
                long earliest = Long.MAX_VALUE;
                for (int m = 0; m < machines; m++) {
                    earliest = Math.min(earliest, freeAt[m]);
                }
                now = earliest;
                best = lightestFree(weights, freeAt, now);
            }
            answer[job] = best;
            freeAt[best] = now + durations[job];
        }
        return answer;
    }

    private static int lightestFree(int[] weights, long[] freeAt, long now) {
        int best = -1;
        for (int m = 0; m < weights.length; m++) {
            if (freeAt[m] <= now && (best < 0 || weights[m] < weights[best])) {
                best = m;
            }
        }
        return best;
    }
}
