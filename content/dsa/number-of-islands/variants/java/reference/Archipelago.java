/** Counts the islands on a text map of land and water. */
public class Archipelago {

    /** Returns the number of islands in rows; '#' is land, '.' is water, only side-sharing cells connect. */
    static int countIslands(String[] rows) {
        int height = rows.length;
        if (height == 0) {
            return 0;
        }
        int width = rows[0].length();
        boolean[] seen = new boolean[height * width];
        int[] stack = new int[height * width];
        int islands = 0;
        for (int start = 0; start < height * width; start++) {
            if (rows[start / width].charAt(start % width) != '#' || seen[start]) {
                continue;
            }
            islands++; // unvisited land: a new island
            int top = 0;
            stack[top++] = start;
            seen[start] = true;
            while (top > 0) {
                int cell = stack[--top];
                int r = cell / width;
                int c = cell % width;
                if (r > 0) top = visit(rows, seen, stack, top, (r - 1) * width + c, width);
                if (r + 1 < height) top = visit(rows, seen, stack, top, (r + 1) * width + c, width);
                if (c > 0) top = visit(rows, seen, stack, top, r * width + c - 1, width);
                if (c + 1 < width) top = visit(rows, seen, stack, top, r * width + c + 1, width);
            }
        }
        return islands;
    }

    /** Pushes the cell and marks it when it is unvisited land; returns the new stack size. */
    private static int visit(String[] rows, boolean[] seen, int[] stack, int top, int cell, int width) {
        if (!seen[cell] && rows[cell / width].charAt(cell % width) == '#') {
            seen[cell] = true; // mark on push so a cell is never stacked twice
            stack[top++] = cell;
        }
        return top;
    }
}
