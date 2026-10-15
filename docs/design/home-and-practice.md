# Home and Practice: what NeetCode does, and what Intervue does instead

Written 2026-10-15 from thirteen screenshots of neetcode.io (the public home page and the signed-in Practice page) the owner supplied. This is the information architecture and page design for Phase 1ac. The palette is in `palette.md`.

## 1. What the screenshots show

### Home (marketing plus a dashboard strip)

Top to bottom: a floating top bar (Courses, Practice, Roadmap, Versus, theme, bell, Pro, avatar); a hero split 50/50, headline left and a topic tree right where every node has a progress bar; a "Get Pro" button; a **Continue** row of two cards (course name, n/m, "Next: <item>" with an arrow); company logos; success stories; **The roadmap** as a tree of cards (title, one line of what the group contains, n/m and a percent with a bar); **Courses** grouped by category, each card with cover art, duration and a difficulty pill; a "Start practicing" panel with a feature list and a card that shows the next problem and the first three topic chips; the founder story; a footer.

### Practice (the working page)

Three columns on a 1900 px window. Left, a sticky **Menu** card: Coding Interviews (expanded: Problems, Company Tagged, Cheatsheets, Quizzes), AI Coding (beta), System Design, Machine Learning, Low Level Design, Databases. Centre: two course cards, then a hero card for the active list ("NeetCode 150", description, Solved n/150, Starred), a list switch (Core Skills / NeetCode 150), a search box, five icon buttons (filter, view, shuffle, clear progress, help), tag chips with counts (Array 624, String 273, Hash Table 233, Expand), then one accordion row per topic group with n/m and a bar, in roadmap order. Right rail: a ring of solved by difficulty (Easy 18/28, Medium 34/101, Hard 1/21), a calendar with the streak marked per day, Current streak and Best streak tiles, a hearts counter, a percentile tile.

### What works

- **"Continue" with a named next item** is the strongest element on both pages: one click back into the work, no choosing.
- **Progress on every topic group** (n/m plus a bar) makes the whole catalogue legible at a glance, and the same shape repeats on home, roadmap and practice, so the user learns it once.
- **One filter bar** above the list: search, tag chips with counts, a view switch, shuffle, reset. Filters are chips, not a form.
- **Grouping by topic in roadmap order** gives the list a direction; the user knows what comes next.
- **The left menu is a card with sections and sub-items**, collapsible, and it carries the whole product's map.
- Difficulty and status are shown with a word and a colour, never the colour alone.

### What does not work, and we will not copy

- **Side space.** The content column is about 1500 px on a 1900 px window on home and the practice page keeps the centre at about 930 px; the top bar on home floats inside further margins. On a 1366 px laptop this is fine, on a desktop a third of the window is empty.
- **Home is marketing first.** A signed-in user scrolls past a hero, logos and testimonials to reach "Continue". We have no marketing inside the app; the website does that.
- **The roadmap tree.** It looks good once; as a working view it wastes vertical space (one node per row, wide gaps, dotted connectors) and the dependency edges carry little information for someone picking the next thing to do. A grouped list in the same order says the same with a fifth of the height.
- **The right rail is noise for us.** Calendar, hearts, percentile and a global solved ring are motivation tools for a free site with a million users. Our user is one person preparing for a round in the next few weeks; a streak and "this week" are enough.
- **Two kinds of card for the same thing.** Courses with cover art next to problem rows without any; we have no video, so no cover art, one card shape.
- **Two scroll positions for the menu.** On the practice page the menu card floats mid-page after a scroll (second practice screenshot); a sidebar should be fixed.

## 2. Intervue's information architecture

The owner's correction: "AI-assisted coding" is not the home page. It is a section inside Practice, next to DSA, as NeetCode's "AI Coding" sits inside its practice menu.

```
Home                      the dashboard: continue, this week, tracks
Practice
  AI-assisted coding      the coding-round section: scenario problems, 3 phases each
  DSA                     the dsa-quick section: NeetCode 150 questions by topic group (Phase 1ad)
Learn                     (later: the walkthroughs as reading material by topic; not in 1ac)
Doctor
Theme
```

The side menu is a fixed column, not a floating card. Sections register themselves by id (the registry from Phase 1ac); the menu lists what the registry holds under Practice, so DSA appears in 1ad with no shell edit. A section's entry opens that section's page; a problem opens the set-up screen; a session opens the room, which hides the menu.

## 3. Layout: no wasted side space

Every page except the room is a two-column grid: the menu (240 px, 64 px collapsed) and the main column, which takes the rest of the window with a 24 px gutter. Inside the main column:

- Up to 1280 px wide: one column. Cards wrap in a grid of `repeat(auto-fill, minmax(320px, 1fr))`.
- Above 1280 px: the main column splits into content (fluid) and a 320 px **aside** that holds the page's secondary blocks (on Home: this week and streak; on a section page: the filters' summary and the "next up" card). Below 1280 px the aside's blocks stack under the content, first.
- Prose pages (Answer, overview, walkthrough) keep a 72ch measure for text, but their chrome (header, tags, buttons) spans the column.
- No page sets a `max-width` on its main column. The room is untouched.

On a 1366 px laptop this gives the menu plus a 1100 px working column; on 1920 px, the menu, a 1300 px content column and the aside. Nothing is centred in a narrow strip.

## 4. Home: the dashboard

Top to bottom, in one scroll of a 1080 px window:

1. **Header line.** "Good afternoon, Kalash." and, in the aside, the streak ("3 days, best 5") computed from `history[].startedAt` dates and a seven-day strip (one dot per day, filled when a session was finished that day). No calendar.
2. **Continue.** One card per open session (title, section, phase k of n, time used, Resume) and, when none is open, one card per section with "Next: <first unsolved in roadmap order>" and Start. At most three cards, side by side.
3. **Tracks.** One card per section, the same shape on Home and on the section page's top:
   - AI-assisted coding: solved n of 10, average best, a bar, and a row of 18 small cells, one per NeetCode topic group, filled according to how many coding-round problems covering that group are solved (`covers[]` from Phase 1ae; until then, by `techniques[]`). This is Intervue's own view: the scenario problems mapped onto the DSA groups.
   - DSA: solved n of 150, a bar, and the same 18 cells by group. Until 1ad the card says "Arrives in the next release" with the cells empty.
   Hovering a cell names the group and its count; clicking it opens the section page filtered to that group.
4. **Recent results.** The last five finished sessions as rows: problem, section, label (Interview or Practice), score, date, with Result and Answer links. Replaces the current History table.

The "Your progress" stat tiles move to the section page; Home shows only what moves the user to the next session.

## 5. Section page (Practice, one per section)

The same page component for both sections, fed by the section's registry entry:

1. **Section header.** Title, the one-paragraph description from `section.json`, the track card from Home (solved, bar, 18 group cells) and, in the aside, "Next up" (the first unsolved in order, Start) plus the stat tiles (solved, attempted, not started, average best, finished sessions).
2. **Filter bar.** A search box (title and tags), then chips: difficulty (Easy, Medium, Hard, multi-select), status (Solved, Attempted, Not started), tag chips with counts (the ten most common, "More" expands), and three buttons: view (Grouped by topic / Flat list), Random unsolved, Reset. Filters AND across kinds and OR within a kind (PLAN.md 7.2). The chosen filters live in the URL-less screen state and are kept per section while the app runs.
3. **The list.** Grouped view: one accordion row per NeetCode topic group in roadmap order (Arrays and hashing, Two pointers, Sliding window, Stack, Binary search, Linked list, Trees, Tries, Heap, Backtracking, Graphs, Advanced graphs, 1-D DP, 2-D DP, Greedy, Intervals, Math and geometry, Bit manipulation), with n/m and a bar in the row head; opened, it shows the problem cards (coding round) or question rows (DSA: title, difficulty, tags, status, "used in" chips). Groups with no match under the current filters are hidden, not shown empty. Flat view: the same cards or rows in one grid, sorted by difficulty then title. A coding-round card keeps its current content (title, difficulty, minutes, state, summary, Resume, Last result, Answer, Start) and gains the tag chips and, from 1ae, "Covers" chips.
4. **Free marker.** The free problems carry a "Free" pill on the card (ADR 0001); nothing else is gated inside the app in 1ac.

## 6. Motion

Only motion that reports a change, all in CSS, all honouring `prefers-reduced-motion`:

- Progress bars and the 18 cells fill from zero to their value on first paint of a page (300 ms ease-out). That is the "track animation": the user sees the bar move to where they are.
- The Continue card's arrow slides 4 px right on hover; cards lift 1 px with a stronger border on hover.
- Accordion rows open with a height transition (150 ms).
- The phase dots in the room header already animate; unchanged.

No carousels, no counters ticking up, no confetti.

## 7. What this changes in the existing code

- `Dashboard.tsx` becomes `HomePage.tsx` (the dashboard above) and `SectionPage.tsx` (the list with filters); `progress.ts` gains per-group counts, the streak and the seven-day strip, all from the history the client already returns (`startedAt`, `finished`, `countsAsSolved`).
- `App.tsx` gains the shell (menu plus main column) around every screen except the room, and the section registry by id.
- `TopBar` stays for the room; the Dashboard button on the result and answer pages becomes the menu's Home entry.
- The engine's `ProblemSummary` gains `tags[]`; nothing else in the engine changes in 1ac.
