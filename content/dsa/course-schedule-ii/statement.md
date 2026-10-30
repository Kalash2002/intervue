# Course Schedule II

A school offers `numCourses` courses numbered `0` to `numCourses - 1`. Each pair `[a, b]` in `prerequisites` says that course `b` must be finished before course `a` can start. A course may list itself, which makes it impossible to take, and the same pair may appear more than once.

Implement `StudyPlan.findOrder(int numCourses, int[][] prerequisites)` in `StudyPlan.java`. Return an array that lists every course exactly once, in an order where each course comes after all of its prerequisites. When several orders work, any of them is accepted. When no order works, return an empty array; a partial order is not accepted.

## Example 1

**Input:** `numCourses = 5`, `prerequisites = [[2,0],[2,1],[4,2],[3,1]]`
**Output:** `[0,1,3,2,4]`
**Explanation:** Courses 0 and 1 need nothing, 3 needs 1, 2 needs 0 and 1, and 4 needs 2. Other orders, such as `[1,3,0,2,4]`, are accepted too.

## Example 2

**Input:** `numCourses = 3`, `prerequisites = [[1,2],[2,1]]`
**Output:** `[]`
**Explanation:** Courses 1 and 2 each need the other. Course 0 is free, but an order must list every course, so there is none.

## Example 3

**Input:** `numCourses = 3`, `prerequisites = []`
**Output:** `[0,1,2]`
**Explanation:** With no pairs at all, every course still has to be listed, and any of the six orders of the three courses is accepted.

## Constraints

- `1 <= numCourses <= 10^5`
- `0 <= prerequisites.length <= 2 * 10^5`
- `prerequisites[i].length == 2`
- `0 <= prerequisites[i][0], prerequisites[i][1] < numCourses`

## Follow-up

Can you build the order in time linear in the number of courses and pairs?
