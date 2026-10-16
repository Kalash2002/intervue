## The question

Given the root of a binary tree, return how many nodes lie on its longest root-to-leaf path; one node is depth 1 and an empty tree is depth 0. The tree may have any shape, including one path through all 100,000 nodes.

What makes it harder than it looks: the textbook answer is one line of recursion, `1 + max(depth(left), depth(right))`, and it is right on paper. In Java it needs one stack frame per level, and a path of 100,000 nodes overflows the default stack long before it finishes. The other slip is counting edges instead of nodes, which makes a single node come out as 0.

## How to think about it

The first idea is to try every path from the root down and keep the longest. Walking every path is a depth-first walk, and if each node carries its own depth (its parent's plus one) there is nothing to recompute: the answer is the largest depth any node carries. Keep the pending nodes on an explicit stack and the walk never touches the call stack.

The recursive version says the same thing more briefly: the depth of a tree is one more than the deeper of its two subtrees. It is the answer most people give first, and it is correct, but it is the one that breaks on the long path.

There is a cleaner way to see depth: it is the number of levels. Process the tree a level at a time with a queue: when a level starts, the queue holds exactly that level's nodes; take them all off, put their children on, and add one to the count. When the queue runs dry, the count is the depth.

Walk Example 1, `[10,4,15,null,7,12,20,null,null,11]`. Level 1 is 10. Level 2 is 4 and 15. Level 3 is 7 (under 4) and 12 and 20 (under 15). Level 4 is 11 alone, under 12. Nothing is below 11, so the depth is 4.

![Example 1 counted level by level: 10, then 4 and 15, then 7, 12 and 20, then 11 alone on level 4](figures/levels.svg)

## Brute force

Walk every path with an explicit stack, each entry carrying its node's depth, and keep the largest. It comes first because "try every path, keep the longest" is what you say first, and it fixes the answer to compare against. It is correct because every node is pushed once, by its parent, with the parent's depth plus one, so it carries its true depth; the deepest leaf is among them.

```java
static int maxDepth(TreeNode root) {
    if (root == null) {
        return 0;
    }
    ArrayDeque<TreeNode> nodes = new ArrayDeque<>();
    ArrayDeque<Integer> depths = new ArrayDeque<>(); // depths.peek() belongs to nodes.peek()
    nodes.push(root);
    depths.push(1); // a lone root already has depth 1: nodes, not edges
    int best = 0;
    while (!nodes.isEmpty()) {
        TreeNode n = nodes.pop();
        int d = depths.pop();
        best = Math.max(best, d);
        if (n.left != null) { nodes.push(n.left); depths.push(d + 1); }
        if (n.right != null) { nodes.push(n.right); depths.push(d + 1); }
    }
    return best;
}
```

**Complexity:** time O(n), extra space O(h) for the stacks, where h is the depth (up to n on a path), all on the heap.

## Better: recursion

The depth of an empty tree is 0; otherwise it is one more than the deeper subtree.

```java
static int maxDepth(TreeNode root) {
    if (root == null) {
        return 0;
    }
    return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
}
```

**Complexity:** time O(n), extra space O(h) for the call stack, one frame per level; a path of 100,000 nodes overflows Java's default stack and the test fails with `StackOverflowError`.

**Prefer it when:** the tree is known to be shallow, such as a balanced search tree, where it is the clearest code and the stack holds only a few dozen frames.

## Optimal: count levels with a queue

Keep a queue holding exactly one level. While it is not empty, add one to the depth, then take off as many nodes as the level has and queue their children. This is the reference solution.

```java
static int maxDepth(TreeNode root) {
    if (root == null) {
        return 0;
    }
    ArrayDeque<TreeNode> level = new ArrayDeque<>();
    level.add(root);
    int depth = 0;
    while (!level.isEmpty()) {
        depth++; // the queue holds exactly one whole level here
        for (int i = level.size(); i > 0; i--) {
            TreeNode n = level.poll();
            if (n.left != null) level.add(n.left);
            if (n.right != null) level.add(n.right);
        }
    }
    return depth;
}
```

**Complexity:** time O(n), extra space O(w) for the queue, where w is the widest level (one node on a path, about n/2 in a complete tree), all on the heap.

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Brute force | O(n) | O(h) on the heap |
| Better: recursion | O(n) | O(h) call stack, overflows on a path of 100,000 nodes |
| Optimal: count levels with a queue | O(n) | O(w) on the heap, the widest level |

## Where it shows up

No coding-round problem is linked to this question. Measuring the depth of a tree shows up when a file-system scan reports how deeply folders are nested, when a parser or JSON validator rejects documents nested beyond a limit, and when a self-balancing tree checks that its height has stayed logarithmic.

## Follow-up answers

**Why does the one-line recursive answer fail here, and what would make it safe?** Each level adds a stack frame, and Java's default thread stack holds roughly ten thousand small frames (fewer while the code still runs interpreted), so a path of 100,000 nodes overflows. Moving the pending work to the heap fixes it: the level queue above, or the explicit stack of the brute force. Running the recursion on a thread created with a larger stack size also works, but only moves the limit.

**How would you find the minimum depth instead?** Count levels the same way, but stop at the first node that has no children and return the current level: breadth-first order reaches the shallowest leaf first, so the walk can end early. A node with one child is not a leaf, so a root with only a right subtree has the depth of that subtree's shallowest leaf, not 1.

**How would you return the nodes on one deepest path, not just its length?** Remember each node's parent while walking (a map from child to parent, or a parent field). The last node taken off the queue lies on the deepest level, so follow the parent links from it back to the root and reverse the list. That is still O(n) time and O(n) extra space for the parent map.
