# How a strong round goes: Number of Islands

## Approach

Every island is a group of land cells connected through their sides, so the question is how many connected groups the grid has. I scan the cells in order. A land cell I have not visited yet must belong to an island nobody has counted, so I add one and then flood outward from it, marking every land cell I can reach through the four neighbours. When the scan moves on, all cells of that island are already marked and it cannot be counted again. I mark a cell when I put it on the stack, not when I take it off, otherwise the same cell is pushed several times. I use my own stack instead of recursion, because a snake-shaped island of half a million cells would overflow the call stack.

```java
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
```

`visit` pushes the neighbour and marks it when it is land and not yet seen, and returns the new stack size.

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Flood fill from each unvisited land cell, cells marked when pushed | O(rows x columns) | O(rows x columns) |
| The same with recursion | O(rows x columns) | O(rows x columns) call stack, which can overflow |
| Union-find over land cells | O(rows x columns x alpha) | O(rows x columns) |

There is no naive solution that is meaningfully slower: every approach has to look at each cell at least once.

## Where it shows up

`friend-suggestion` walks a friendship graph with the same visited-mark discipline: breadth-first from one user, claiming each person when they are queued so nobody is reached twice. A grid is a graph whose edges are the four neighbours, so the idea carries straight over, and "which people are connected to this one" is the island question on a network.

## Follow-up answers

**How would you also report the size of the largest island?** Count the cells popped during each flood and keep the maximum over islands. It is the same pass with one extra counter.

**What if land can be added one cell at a time and you must report the count after each addition?** Use union-find. Adding a cell makes one more island, then each land neighbour already present is merged with it, and every merge that joins two different sets lowers the count by one. Each update is close to constant time, instead of re-scanning the grid.

**What if diagonal neighbours also connect land?** Visit eight neighbours instead of four. Nothing else changes.
