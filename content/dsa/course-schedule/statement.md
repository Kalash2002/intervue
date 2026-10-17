# Course Schedule

A school offers `numCourses` courses numbered `0` to `numCourses - 1`. Each pair `[a, b]` in `prerequisites` says that course `b` must be finished before course `a` can start. A course may list itself, which makes it impossible to take, and the same pair may appear more than once.

Implement `Curriculum.canFinish(int numCourses, int[][] prerequisites)` in `Curriculum.java`. Return `true` if there is an order in which a student can finish every course, taking one course at a time and each only after all of its prerequisites, and `false` otherwise.

## Example 1

**Input:** `numCourses = 4`, `prerequisites = [[1,0],[2,1],[3,1],[3,2]]`
**Output:** `true`
**Explanation:** Take course 0, then 1, then 2, and finally 3, which needs both 1 and 2.

## Example 2

**Input:** `numCourses = 3`, `prerequisites = [[0,2],[1,0],[2,1]]`
**Output:** `false`
**Explanation:** Course 0 needs 2, course 2 needs 1 and course 1 needs 0, so none of the three can be taken first.

## Example 3

**Input:** `numCourses = 2`, `prerequisites = [[1,1]]`
**Output:** `false`
**Explanation:** Course 1 must be finished before it can start, so it can never be taken, even though course 0 is free.

## Constraints

- `1 <= numCourses <= 10^5`
- `0 <= prerequisites.length <= 2 * 10^5`
- `prerequisites[i].length == 2`
- `0 <= prerequisites[i][0], prerequisites[i][1] < numCourses`

## Follow-up

Can you answer in time linear in the number of courses and pairs?
