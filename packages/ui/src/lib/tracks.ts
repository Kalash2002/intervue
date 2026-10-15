// The 18 NeetCode 150 topic groups in roadmap order (PLAN.md 7.2), and which technique ids from
// problem.json fall in each. Techniques outside every group (sorting, strings) stay filterable as
// tags and count in no group. Owns the list only; progress counts live in progress.ts.

export interface Track {
  id: string;
  title: string;
  /** Technique ids that count towards this group; the id itself always does. */
  techniques: readonly string[];
}

const track = (id: string, title: string, ...aliases: string[]): Track => ({
  id,
  title,
  techniques: [id, ...aliases],
});

export const TRACKS: readonly Track[] = [
  track('arrays-hashing', 'Arrays and hashing', 'arrays', 'hashing'),
  track('two-pointers', 'Two pointers'),
  track('sliding-window', 'Sliding window'),
  track('stack', 'Stack'),
  track('binary-search', 'Binary search'),
  track('linked-list', 'Linked list'),
  track('trees', 'Trees'),
  track('tries', 'Tries'),
  track('heap', 'Heap and priority queue', 'priority-queue'),
  track('backtracking', 'Backtracking'),
  track('graphs', 'Graphs', 'bfs', 'dfs'),
  track('advanced-graphs', 'Advanced graphs', 'dijkstra', 'union-find'),
  track('dp-1d', '1-D dynamic programming', 'dynamic-programming'),
  track('dp-2d', '2-D dynamic programming'),
  track('greedy', 'Greedy'),
  track('intervals', 'Intervals'),
  track('math-geometry', 'Math and geometry', 'math'),
  track('bit-manipulation', 'Bit manipulation', 'bits'),
];

/** The first group a problem's techniques fall in, in roadmap order; null when none does. */
export function trackOf(techniques: readonly string[]): Track | null {
  return TRACKS.find((t) => techniques.some((id) => t.techniques.includes(id))) ?? null;
}
