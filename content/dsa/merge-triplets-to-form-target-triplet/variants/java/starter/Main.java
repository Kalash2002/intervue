public class Main {
    public static void main(String[] args) {
        int[][] presets = {{40, 10, 90}, {60, 20, 30}, {50, 80, 10}, {20, 30, 70}};
        System.out.println(Lights.canMix(presets, new int[] {60, 30, 90}));
    }
}
