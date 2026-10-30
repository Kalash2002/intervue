import static kit.Check.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(String start, String[][] tickets, List<String> want) {
        List<String> got = Itinerary.route(tickets, start);
        assertEquals((long) want.size(), (long) got.size(), "number of airports in the route");
        for (int i = 0; i < want.size(); i++) {
            assertEquals("\"" + want.get(i) + "\"", "\"" + got.get(i) + "\"", "airport " + i + " of the route");
        }
    }

    private static void expect(String start, String[][] tickets, String... want) {
        expect(start, tickets, List.of(want));
    }

    @Test(order = 1)
    void sample() {
        expect("VNO", new String[][] {{"VNO", "KUN"}, {"KUN", "VNO"}, {"VNO", "RIX"}, {"RIX", "VNO"}},
                "VNO", "KUN", "VNO", "RIX", "VNO");
        expect("LIS", new String[][] {{"LIS", "FAO"}, {"LIS", "OPO"}, {"OPO", "MAD"}, {"MAD", "LIS"}},
                "LIS", "OPO", "MAD", "LIS", "FAO");
    }

    @Test(order = 2)
    void theSameTicketTwice() {
        expect("BCN", new String[][] {{"BCN", "MAD"}, {"MAD", "BCN"}, {"BCN", "MAD"}}, "BCN", "MAD", "BCN", "MAD");
        expect("AMS", new String[][] {{"BRU", "AMS"}, {"AMS", "BRU"}, {"AMS", "BRU"}, {"BRU", "AMS"}},
                "AMS", "BRU", "AMS", "BRU", "AMS");
    }

    @Test(order = 3)
    void oneTicket() {
        expect("CPH", new String[][] {{"CPH", "ARN"}}, "CPH", "ARN");
    }

    @Test(order = 4)
    void aDeadEndAwayFromTheStart() {
        // at VIE, BUD comes first alphabetically but nothing leaves BUD, so it must be the last stop
        expect("PRG", new String[][] {{"VIE", "BUD"}, {"PRG", "VIE"}, {"LJU", "VIE"}, {"VIE", "LJU"}},
                "PRG", "VIE", "LJU", "VIE", "BUD");
    }

    @Test(order = 5)
    void codesThatDifferOnlyInTheSecondLetter() {
        expect("KRK", new String[][] {{"KRK", "WRO"}, {"WRO", "KRK"}, {"KRK", "WAW"}, {"WAW", "KRK"}},
                "KRK", "WAW", "KRK", "WRO", "KRK");
    }

    @Test(order = 6)
    void randomTripsAgainstAnExhaustiveSearch() {
        Random random = new Random(73);
        String[] airports = {"DUB", "ORK", "SNN", "NOC"};
        for (int round = 0; round < 60; round++) {
            int length = 1 + random.nextInt(8);
            String[] walk = new String[length + 1];
            walk[0] = airports[random.nextInt(airports.length)];
            for (int i = 1; i <= length; i++) {
                do {
                    walk[i] = airports[random.nextInt(airports.length)];
                } while (walk[i].equals(walk[i - 1]));
            }
            List<String[]> tickets = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                tickets.add(new String[] {walk[i], walk[i + 1]});
            }
            Collections.shuffle(tickets, random);
            String[][] given = tickets.toArray(new String[0][]);
            expect(walk[0], given, smallest(given, walk[0]));
        }
    }

    /** Tries tickets in alphabetical order of destination, so the first complete route is the smallest. */
    private static List<String> smallest(String[][] tickets, String start) {
        String[][] sorted = tickets.clone();
        Arrays.sort(sorted, Comparator.comparing((String[] t) -> t[1]));
        List<String> path = new ArrayList<>(List.of(start));
        extend(sorted, new boolean[sorted.length], path);
        return path;
    }

    private static boolean extend(String[][] sorted, boolean[] used, List<String> path) {
        if (path.size() == sorted.length + 1) {
            return true;
        }
        String here = path.get(path.size() - 1);
        for (int i = 0; i < sorted.length; i++) {
            if (!used[i] && sorted[i][0].equals(here)) {
                used[i] = true;
                path.add(sorted[i][1]);
                if (extend(sorted, used, path)) {
                    return true;
                }
                used[i] = false;
                path.remove(path.size() - 1);
            }
        }
        return false;
    }

    private static String code(int i) {
        return "" + (char) ('A' + i / 676) + (char) ('A' + i / 26 % 26) + (char) ('A' + i % 26);
    }

    @Test(order = 7, timeoutMs = 3000)
    void tenLapsOfARingOfTenThousandAirports() {
        int airports = 10_000;
        int laps = 10;
        List<String[]> tickets = new ArrayList<>();
        for (int lap = 0; lap < laps; lap++) {
            for (int i = 0; i < airports; i++) {
                tickets.add(new String[] {code(i), code((i + 1) % airports)});
            }
        }
        Collections.shuffle(tickets, new Random(5));
        List<String> want = new ArrayList<>();
        for (int j = 0; j <= airports * laps; j++) {
            want.add(code(j % airports)); // 100,000 tickets in one chain: a recursive search overflows
        }
        expect(code(0), tickets.toArray(new String[0][]), want);
    }

    @Test(order = 8, timeoutMs = 600)
    void aBranchThatStrandsTheCourierAfterManyLoops() {
        int loops = 11;
        List<String[]> tickets = new ArrayList<>();
        tickets.add(new String[] {"KEF", "BGO"});
        tickets.add(new String[] {"KEF", "TOS"});
        tickets.add(new String[] {"TOS", "KEF"});
        List<String> want = new ArrayList<>(List.of("KEF", "TOS", "KEF", "BGO"));
        for (int i = 0; i < loops; i++) {
            String spoke = "PA" + (char) ('A' + i);
            tickets.add(new String[] {"BGO", spoke});
            tickets.add(new String[] {spoke, "BGO"});
            want.add(spoke);
            want.add("BGO");
        }
        // flying KEF to BGO first uses every loop before finding KEF to TOS stranded, in any loop order
        expect("KEF", tickets.toArray(new String[0][]), want);
    }
}
