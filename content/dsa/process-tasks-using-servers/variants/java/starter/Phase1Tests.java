import static kit.Check.assertEquals;

import java.util.Arrays;
import kit.Test;

public class Phase1Tests {

    private static void expect(int[] weights, int[] durations, int[] expected) {
        assertEquals(Arrays.toString(expected), Arrays.toString(Dispatcher.assign(weights, durations)));
    }

    @Test(order = 1)
    void sample() {
        expect(new int[] {4, 2, 7}, new int[] {3, 3, 3, 1, 1}, new int[] {1, 0, 2, 1, 1});
        expect(new int[] {1, 1}, new int[] {5, 5, 1, 1}, new int[] {0, 1, 0, 0});
    }

    @Test(order = 2)
    void oneMachineDoesEverythingInTurn() {
        expect(new int[] {9}, new int[] {2, 2, 2}, new int[] {0, 0, 0});
    }

    @Test(order = 3)
    void moreMachinesThanJobsTiesGoToTheLowerNumber() {
        // machines 1 and 3 weigh the same, so job 0 takes 1 and job 1 takes 3; then the machine of weight 2
        expect(new int[] {3, 1, 2, 1}, new int[] {5, 5, 5}, new int[] {1, 3, 2});
    }

    @Test(order = 4)
    void waitingJobsTakeMachinesInArrivalOrder() {
        expect(new int[] {5, 2, 3}, new int[] {4, 4, 4, 1, 1, 1}, new int[] {1, 2, 0, 1, 1, 2});
    }

    @Test(order = 5)
    void aMachineFreeingAtTheSecondOfArrivalIsFree() {
        // machine 0 frees at second 2, exactly when job 2 arrives
        expect(new int[] {1, 5}, new int[] {2, 9, 1}, new int[] {0, 1, 0});
    }

    @Test(order = 6)
    void identicalMachinesFillInNumberOrder() {
        expect(new int[] {1, 1, 1}, new int[] {10, 10, 10, 10}, new int[] {0, 1, 2, 0});
    }

    @Test(order = 7)
    void heavyMachineUsedOnlyWhenTheLightOneIsBusy() {
        expect(new int[] {10, 1}, new int[] {3, 1, 1, 1, 1}, new int[] {1, 0, 0, 1, 1});
    }

    @Test(order = 8)
    void finishingSecondsBeyondTheIntRange() {
        // machine 1 runs jobs 0, 2 and 4 back to back and is busy until second 3,000,000,000, past the largest int
        // (2,147,483,647); job 5 must therefore wait for machine 0, while an int clock sees machine 1 as free
        expect(new int[] {5, 3}, new int[] {1_000_000_000, 1_000_000_000, 1_000_000_000, 1_000_000_000, 1_000_000_000, 1, 1},
                new int[] {1, 0, 1, 0, 1, 0, 0});
    }

    @Test(order = 9, timeoutMs = 3500)
    void twoHundredThousandJobsOnOneHundredFiftyThousandMachines() {
        int m = 150_000;
        int[] weights = new int[m];
        int[] rank = new int[m]; // rank[r] is the machine with the r-th smallest weight
        for (int i = 0; i < m; i++) {
            weights[i] = (int) ((i * 7919L) % m) + 1; // 7919 is coprime with 150,000, so the weights are 1 to m, once each
            rank[weights[i] - 1] = i;
        }
        int jobs = 200_000;
        int[] durations = new int[jobs];
        Arrays.fill(durations, m);
        int[] expected = new int[jobs];
        for (int j = 0; j < jobs; j++) {
            expected[j] = rank[j % m]; // job j starts the very second the machine of job j - m frees up
        }
        assertEquals(Arrays.toString(expected), Arrays.toString(Dispatcher.assign(weights, durations)));
    }
}
