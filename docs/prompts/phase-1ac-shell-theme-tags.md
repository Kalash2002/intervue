# Phase 1ac: app shell with a side menu, the purple theme, tags, and the Java-first re-plan

## Context to read first

`CLAUDE.md`; `docs/plan/PLAN.md` sections 1 (languages and sections as the user sees them, filtering), 2.6 (sections), 7.2 (taxonomy), 10 (roadmap); `docs/design/palette.md`; `docs/design/home-and-practice.md` (the information architecture, the Home dashboard, the section page with its filter bar, the layout rule for side space, and the motion rules: this prompt implements that document); `docs/contracts/problem-format.md`, `section-contract.md`; `docs/process/*`; the Phase 1ab code in `packages/ui` (`App.tsx`, `pages/*`, `styles/*`) and `sections/coding-round/ui`; the reference screenshots the owner supplied in `docs/plans/phase-1ac-reference/` (local): Hello Interview's dashboard and NeetCode's home and practice pages, analysed in `home-and-practice.md`; take the structure, not the look.

## Goal

The app opens on a Home dashboard (continue, tracks, recent results) with a fixed left menu whose Practice group reaches every section (AI-assisted coding now, DSA next); each section has one page with a filter bar and a grouped list; every page except the room uses the window's width; the whole app wears the dark grey and purple palette; and every problem shows its tags.

## In scope

- Re-plan (docs only, before any code): the first product ships with Java only. In `PLAN.md` section 10 move Python, JavaScript/TypeScript, Go and Linux (today Phases 4, 5, 6) after the sections work, renumbered; pull the DSA section forward as Phase 1ad and the catalogue growth as Phase 1ae (their prompts exist); state that launch targets Windows, with macOS built on CI on every push but hand-tested only when the owner gets a machine (the CI matrix keeps `macos-latest`, marked `continue-on-error`, never dropped); note that signing certificates and the AI API key are not available yet, so Phase 1b keeps the developer route as its only AI route until the key exists and Phase 1c ships an unsigned Windows installer first. Update sections 1 and 7.3 to match, `docs/status.md`, and add a one-paragraph ADR 0007 "Java and Windows first" recording the change and why.
- Shell layout (`packages/ui`), as `home-and-practice.md` section 3: a fixed left menu, 240 px (64 px collapsed, on a button and under 900 px), present on every screen except the room: the wordmark; Home; a Practice group listing every section in the registry (today "AI-assisted coding"; "DSA" appears when its package lands in 1ad); Doctor and the theme switch at the bottom. The main column takes the rest of the window with a 24 px gutter and no `max-width`; above 1280 px it splits into content and a 320 px aside, below that the aside's blocks stack first. Cards use an auto-fill grid. Prose keeps a 72ch measure, chrome spans the column. The room keeps its full-width layout and its top bar unchanged; its "← Dashboard" goes Home.
- Home page, as section 4: greeting and the streak with a seven-day strip in the aside (both computed from history dates); Continue (open sessions, else "Next: ..." per section, at most three cards); one track card per section (solved n of m, a bar, 18 topic-group cells filled by solved problems whose techniques fall in the group; the DSA card says "Arrives in the next release" until 1ad); Recent results (last five finished sessions with Result and Answer links). The stat tiles move to the section page.
- Section page, as section 5, one component for every section fed by its registry entry: header with the section's description from `section.json`, the track card, and in the aside "Next up" and the stat tiles; a filter bar (search over title and tags; chips for difficulty, status and the ten most common tags with counts, "More" expands; view switch Grouped by topic or Flat; Random unsolved; Reset; AND across kinds, OR within, kept per section while the app runs); the list grouped by the 18 NeetCode topic groups in roadmap order as accordion rows with n/m and a bar, empty groups hidden, or flat sorted by difficulty then title. Cards keep their current content plus tag chips and a "Free" pill on the free problems.
- Motion, as section 6: bars and cells fill from zero on first paint, hover lift on cards, the Continue arrow nudge, accordion height transition; CSS only, all off under `prefers-reduced-motion`.
- Theme: `tokens.css` rebuilt from `docs/design/palette.md` for both themes, dark default; status colours get their word or icon; the Home wash at most 4.5% in dark; syntax colours re-checked for 4.5:1 on the new background; the assistant panel uses the chat set. No other stylesheet gets a hex value.
- Tags: `problem.json` gains the optional field `tags[]` (free text, lower-case, for example `graphs`, `bfs`, `fintech`, `neetcode-150`) next to the existing `techniques[]` and `domain`; the contract notes it as additive, format version unchanged. The engine returns tags in `ProblemSummary` and the session view. Tags show as chips on the problem card, the set-up screen, the Answer page and the result page. The result page's header links to the Answer page and to each tag's filtered list. All ten problems get tags.
- Walkthrough links: every `neetcode.io` and `leetcode.com` link in `content/problems/*/walkthrough.md` becomes an in-app link `dsa://<question-id>` that the Markdown renderer shows as a chip with the question's title; until 1ad the chip opens a "DSA arrives in the next release" toast. Question ids are the NeetCode slugs (`cheapest-flight-path`, `contains-duplicate`); keep a list of every id referenced in `content/dsa/INDEX.md` (title, NeetCode topic group, the walkthroughs that reference it) so 1ad writes those first.

## Out of scope

The DSA section itself, new problems, backend, accounts, packs, payments, signing, the updater, any language other than Java, any change inside the room's editor, output pane or timeline.

## Constraints

- `CLAUDE.md` boundaries: the section's UI stays in `sections/coding-round/ui`; the shell only learns a section's id, title and main page through `SectionUi`. Add the section registry by id noted under "Later" in `docs/status.md` now, with the coding round as its only entry.
- No new dependency; no design library; CSS tokens only. Keep every Phase 1ab test green; the layout tests in `sections/coding-round/ui/test/layout.test.tsx` still pass unchanged.
- `tags[]` is optional; a problem without it still loads (test with a fixture).
- Screenshots of every page in both themes go to `docs/plans/phase-1ac-evidence/` (local) with the headless Edge route used in 1ab.

## Exit criteria

1. On Windows at 1366x768 and 1920x1080 the Home, section, set-up, overview, result, answer and Doctor pages show the fixed side menu and use the main column's width with no empty bands left and right (the aside sits beside the content from 1280 px, so at both sizes; below 1280 it stacks first); the room is unchanged. Evidence: screenshots in both themes.
2. Every colour in `packages/ui/src/styles/*.css` comes from `tokens.css`, and every `tokens.css` value comes from `docs/design/palette.md` (a grep for six-digit hex values outside `tokens.css` finds nothing). Contrast of body text, muted text and the five syntax colours on the dark background is at least 4.5:1 (list the measured ratios).
3. Every problem card, set-up screen, Answer page and result page shows the problem's tags; clicking a tag filters the section page; the result page reaches the Answer page in one click. Home shows Continue with the right next problem, the track card's 18 cells match the solved problems' techniques, and the streak matches the history dates (unit tests on `progress.ts` for the cells, the streak and the next-up choice). The filter bar's search, difficulty, status, tag chips, Grouped and Flat views and Random unsolved each do what section 5 of `home-and-practice.md` says (component tests).
4. A grep for `neetcode.io` and `leetcode.com` under `content/problems` finds nothing; `content/dsa/INDEX.md` lists every referenced question id with its title and topic group.
5. The roadmap, status board, prompts index and ADR 0007 say Java and Windows first, with macOS CI kept; all gates of Phase 1ab are green, including the macOS CI job building (it may be marked allowed-to-fail, but it must build on this run).

## Gates

Rows marked Phase 1a in `docs/process/definition-of-done.md`.

## Deliverables

Code and tests; `docs/contracts/problem-format.md` (tags); `docs/adr/0007-java-windows-first.md`; `PLAN.md` sections 1, 7.3 and 10; `content/dsa/INDEX.md`; `CHANGELOG.md`; `docs/status.md`; `docs/runbooks/commands.md`.

## Report

What shipped, each exit criterion met or not with evidence, the contrast table, the screens the owner should look at by hand in both themes, what moved to "Later".
