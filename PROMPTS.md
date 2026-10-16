# Prompt sequence for Claude Code

The exact prompts to give Claude Code, in order, one phase at a time. Give one prompt, wait for the report, do the human steps it names, then give the next. Never skip a gate to save time; the pipeline stops on red for a reason.

Conventions: lines starting with `>` are things you type into Claude Code. Lines starting with `$` are shell commands you run yourself. "Human" marks work only you can do.

---

## 0. One-time setup (before Phase 0)

Human: create the GitHub repository, protect `main` with required status checks, and do the first commit yourself.

```
$ cd D:\projects\AICoding\intervue
$ git init -b main
$ git add -A --dry-run
```

Check the dry run against the table in `CONTRIBUTING.md`: no `docs/plan/research/`, no `PLANNING-PROMPT.md`, no `prototype/sessions/<id>/`, nothing from `.claude/*.local.*`. Then:

```
$ git add -A && git commit -m "chore: plan, process layer, phase prompts, learning files"
```

Inside Claude Code, in this folder:

```
> /plugin marketplace add obra/superpowers-marketplace
> /plugin install superpowers@superpowers-marketplace
```

In a shell, with your key from context7.com:

```
$ claude mcp add --scope user --header "CONTEXT7_API_KEY: <your key>" --transport http context7 https://mcp.context7.com/mcp
```

Back in Claude Code, check that everything is visible and that the agents know the repository:

```
> Read CLAUDE.md, docs/process/pipeline.md and docs/status.md. List the subagents in .claude/agents and the skills available to you, confirm the Context7 tools are present, and tell me anything missing before we start Phase 0. Do not change any file.
```

---

## Phase 0: validation (2 weeks, mostly human)

Human first: `docs/learning/phase-0.md`.

Prompt 0.1, the landing page:

```
> Build the Phase 0 landing page from PLAN.md section 13 days 3 to 5: a React + Vite + MUI site in web/ with the promise, three prototype screenshots I will drop into web/public/screens/, a waitlist form (role, target companies, language, what they would pay) posting to a Cloudflare Pages function that appends to a KV namespace, and PostHog page events. Deploy config for Cloudflare Pages included. Keep it one page. Follow CLAUDE.md standards and open a PR.
```

Prompt 0.2, the prototype download:

```
> From prototype/, run java tools/Verify.java to confirm all three problems pass, then build a zip of the prototype with a one-page RUN.md for engineers who have a JDK, place it under web/public/downloads/, and link it from the landing page as "early prototype". Do not change prototype code.
```

Prompt 0.3, interview materials:

```
> Write docs/discovery/interview-script.md (8 questions, 20 minutes, non-leading, for Indian working engineers and students preparing for AI-assisted coding rounds), docs/discovery/pricing-survey.md (three price points: ₹1,500 monthly, ₹3,500 quarterly, and an open question), and docs/discovery/ca-questions.md from PLAN.md section 8.4. Short, printable.
```

Human: interviews, testers, CA meeting, Apple and certificate applications, days 11 to 14 of the Rust sprint start.

Prompt 0.4, the review:

```
> Using my notes in docs/discovery/, write docs/discovery/phase-0-review.md: waitlist count, would-pay count, price sensitivity, tester findings, and a go or adjust recommendation for Phase 1a scope. Then update docs/status.md. Record ADRs 0001-payments-mock-then-gateway.md and 0002-entity-and-gst.md in docs/adr/ from what I tell you here: <paste your decisions>.
```

---

## Phase 1a: base working app with 10 problems

Human first: the Rust and Tauri sprint in `docs/learning/phase-1a.md`, until its checklist is ticked.

Prompt 1a.1:

```
> /phase docs/prompts/phase-1a-base-app.md
```

The pipeline runs diagnose, research, plan, design review, implementation, simplify, review, gates and report. It will stop and tell you when it needs a decision or when a gate is red. Standard replies are in "Follow-up prompts" below.

Human: try the app on Windows and macOS as the report asks: an Interview session and a Practice session on the free problem, hints, an undo of a replayed edit, reset problem, autocomplete, an inline compile error. Send the zip to one tester.

Prompt 1a.2, close the phase:

```
> Manual results for Phase 1a: <what you tried, on which OS, what you saw, the tester's notes>. Record them in docs/status.md, fix anything blocking, re-run the gates, and tell me whether every exit criterion in docs/prompts/phase-1a-base-app.md is met. If yes, tag app-v0.1.0 and write docs/retro/phase-1a.md with what took longer than planned and why.
```

---

## Phase 1ab: live assistant on the developer route, scoring rule, room refresh

Added 2026-10-14 after the first hands-on run of Phase 1a. Run it after Phase 1a is closed (or in parallel on its own branch if the manual checks of 1a are still pending). Human first: `claude` installed and signed in on this machine, and `developer.mode` on in the engine settings.

Prompt 1ab.1:

```
> /phase docs/prompts/phase-1ab-assistant-room.md
```

Human: with developer mode on, run one Interview session on a free problem: ask "fix it" and check you get a place and the logic with no code; ask for an explanation; state an approach and use Write code on the named method; undo it; finish and read the review; check the dashboard counts the session as solved. Run one Practice session: switch the rules, reveal a hint and ask for it to be explained. Look at every screen in both themes.

Prompt 1ab.2, close the phase:

```
> Manual results for Phase 1ab: <what you tried, what you saw, screens that still look wrong>. Record them in docs/status.md, fix anything blocking, re-run the gates, and tell me whether every exit criterion in docs/prompts/phase-1ab-assistant-room.md is met. If yes, tag app-v0.1.1 and write docs/retro/phase-1ab.md.
```

---

## Phase 1ac: side menu, full-width pages, purple theme, tags, Java-first re-plan

Added 2026-10-15 after the Phase 1ab hand test. Run it before anything else: the first product is Java only on Windows, the home and every page except the room get a left side menu and the window's width, and the palette in `docs/design/palette.md` replaces the current tokens. Human first: nothing; the Hello Interview screenshot is already in `docs/plans/phase-1ac-reference/`.

Prompt 1ac.1:

```
> /phase docs/prompts/phase-1ac-shell-theme-tags.md
```

Human: open the app on Windows at your usual window size and at full screen, in both themes. Walk Home, the AI-assisted coding page, a set-up screen, an overview, a result and an Answer page, and the Doctor: the side menu is on every one of them, nothing is squeezed into a narrow middle column, and the room is unchanged. Click a tag on a card and on a result page. Click a `dsa://` chip in a walkthrough and see the "next release" toast.

Prompt 1ac.2, close the phase:

```
> Manual results for Phase 1ac: <what you saw, pages that still have empty bands, colours that look wrong>. Record them in docs/status.md, fix anything blocking, re-run the gates, and tell me whether every exit criterion in docs/prompts/phase-1ac-shell-theme-tags.md is met. If yes, tag app-v0.1.2 and write docs/retro/phase-1ac.md.
```

---

## Phase 1ad: DSA section with the NeetCode 150 set

Runs after 1ac. The section lands first with the questions the walkthroughs already reference, then the rest in topic-group batches; each batch is its own PR.

Prompt 1ad.1:

```
> /phase docs/prompts/phase-1ad-dsa-section.md
```

Human after the first batch: open DSA from the side menu, solve `two-sum` in Interview and in Practice, read its walkthrough, then open a coding-round walkthrough and follow a `dsa://` chip to its question and back.

Prompt 1ad.2, repeat per batch until all 150 verify:

```
> Continue Phase 1ad with the next topic-group batch from content/dsa/INDEX.md (10 to 20 questions), verify them three ways, update INDEX.md and docs/status.md, and open the PR.
```

Prompt 1ad.2b, the hand-test fixes (added 2026-10-16 after the owner's first DSA session; run before closing the phase):

```
> /phase docs/prompts/phase-1ad-fixes-dsa.md
```

Human: open a DSA question in Interview and in Practice: no Assistant tab, Hints in both; the statement shows Example blocks and Constraints like a LeetCode page; type `Map<Integer, Integer> m = new HashMap<>();` and run; read the Answer page from the brute force down to the optimal approach.

Prompt 1ad.3, close the phase:

```
> Manual results for Phase 1ad: <...>. Record them, fix blockers, re-run gates, confirm every exit criterion in docs/prompts/phase-1ad-dsa-section.md including 150 verified questions. Tag app-v0.1.3 and write docs/retro/phase-1ad.md.
```

---

## Phase 1ae: catalogue to 24 Java problems, each covering two or three NeetCode questions

Runs after 1ad. Problems land in batches of 3 to 5.

Prompt 1ae.1:

```
> /phase docs/prompts/phase-1ae-catalogue-java.md
```

Human: play one new problem end to end in Interview mode; on its card and result page follow a "Covers" chip to the DSA question and the "Used in" link back.

Prompt 1ae.2, repeat per batch:

```
> Continue Phase 1ae with the next 3 to 5 problems from the coverage map in PLAN.md 7.3, each with tags[] and two or three covers[] ids, verified three ways on CI, and open the PR.
```

Prompt 1ae.3, close the phase:

```
> Manual results for Phase 1ae: <...>. Record them, fix blockers, re-run gates, confirm every exit criterion in docs/prompts/phase-1ae-catalogue-java.md. Tag app-v0.2.0 and write docs/retro/phase-1ae.md.
```

---

## Phase 1af: Java IntelliSense (moved up from Phase 2, ADR 0009)

Runs after 1ae. Human first: nothing; the JDT language server downloads on first use.

Prompt 1af.1:

```
> /phase docs/prompts/phase-1af-java-intellisense.md
```

Human: in a Java session type `new HashMap<>()` without an import and accept the completion; hover `HashMap`; jump from a call to a project class into its file; turn the flag off and check the built-in completion still works.

Prompt 1af.2, close the phase:

```
> Manual results for Phase 1af: <...>. Record them, fix blockers, re-run gates, confirm every exit criterion in docs/prompts/phase-1af-java-intellisense.md. Tag app-v0.3.0 and write docs/retro/phase-1af.md.
```

---

## Order from here (ADR 0009, 2026-10-16)

After 1af: Phase 2 is the catalogue to 60 (`phase-7-dsa-section.md`, content only); Phase 3 the website and installers (`phase-1c-website-installers-launch.md`, edited first to drop the store, sign-in and the paid pack: ADR 0010); Phase 4 the system design section (`phase-8-system-design.md`); Phase 5 accounts, packs, the payment page, the AI gateway and admin on the mock adapters and a recorded model client (the 1b section below, edited first to drop the real gateway adapter and the provider key); Phase 6 sync; Phase 7 the website dashboard; Phase 8 everything that needs a credential, the real payment gateway, the provider key, signing and notarization (ADR 0011, prompt written when it is near); then the other languages (Phases 9 to 11) and industry mode. The sections below keep their original headings.

---

## Phase 1b: accounts, packs, payment page, AI gateway

Note (2026-10-15): the AI API key and the signing certificates are not available yet. Phase 1b runs with the developer route as its only AI route until the key exists; Phase 1c ships an unsigned Windows installer first and adds signing and macOS when the certificates and a macOS machine arrive. Edit both prompts before running them.

Human first: `docs/learning/phase-1b.md`. Have the E2E Networks account, the domain and the Anthropic API key ready; put secrets only in the places `.env.example` names.

Prompt 1b.1:

```
> /phase docs/prompts/phase-1b-accounts-packs-ai.md
```

Human: sign in from the app on both machines, redeem a code, place a manual UPI order and confirm it in the admin page, download the pack, go offline and open it, let it expire by editing the date on staging, come back online and watch it lock and delete. Use Ask, Write code and a hint explanation; undo an edit; check the usage counters. Switch developer mode on and watch the CLI route and the backend route both get used.

Prompt 1b.2:

```
> Manual results for Phase 1b: <...>. Record them, fix blockers, re-run gates, confirm exit criteria including 4b (route selection branches), compare the measured AI cost per message and per session from the usage log with PLAN.md 9.1 and update 9.1 if they differ by more than 20%. Tag backend-v0.1.0 and app-v0.2.0. Write docs/retro/phase-1b.md.
```

---

## Phase 1c: website, signed installers, launch

Human first: `docs/learning/phase-1c.md`. Have the Apple membership active, the Developer ID certificate exported, the Windows certificate issued, and the signing credentials in GitHub Environment secrets. Generate the updater key pair when the pipeline tells you to.

Prompt 1c.1:

```
> /phase docs/prompts/phase-1c-website-installers-launch.md
```

Human: install from the website on clean Windows and macOS machines, time download to first session, run the update from the previous version, recruit five beta testers with redeem codes.

Prompt 1c.2, beta:

```
> Beta results: <tester notes, funnel numbers from PostHog, crashes from Sentry>. Triage into fix-now and later, fix the fix-now list through the pipeline's review and gates, re-run the installer smoke test on both runners, and prepare the stable release notes.
```

Prompt 1c.3, launch:

```
> Promote the current beta build to the stable channel, verify the updater offers it to a beta install, publish the launch pack as version 1 through content/tools, update the download page, write CHANGELOG entries under app-v1.0.0, backend-v1.0.0 and pack-launch-v1, and write docs/retro/phase-1.md covering 1a to 1c.
```

---

## Phase 2: sync, deferred feedback, Java IntelliSense

Human first: `docs/learning/phase-2.md`.

```
> /phase docs/prompts/phase-2-sync-feedback.md
```

Human: two devices, one offline session, reconnect and request feedback; turn the Java language-server flag on for your account and compare completion before and after.

```
> Manual results for Phase 2: <...>. Record, fix, re-run gates, confirm exit criteria, tag app-v1.1.0 and backend-v1.1.0, publish pack-b-v1, write docs/retro/phase-2.md.
```

---

## Phase 3: website dashboard, catalogue to 36, authoring workbench

Human first: `docs/learning/phase-3.md`. Decide the payment gateway now if you have not; paste the decision into the prompt.

```
> /phase docs/prompts/phase-3-dashboard-content.md
```

Content in this phase comes in batches through the workbench. Repeat this prompt per coverage cell until 36 is reached:

```
> Using the authoring workbench flow in PLAN.md 7.4, draft the problem for coverage cell "<technique>, <domain>, <difficulty>" into content/problems/<id>/ with brief, three-level hints per phase, Java starter with the planted bug, read-only tests, reference, naive, walkthrough and grader notes. Run content/tools verify on it. Show me the brief and the hints for review before opening the PR.
```

```
> Manual results for Phase 3: <...>. Record, fix, re-run gates, confirm exit criteria, tag, publish the new packs and the bundle plan, write docs/retro/phase-3.md.
```

---

## Phase 4: Python

Human first: `docs/learning/phase-4.md`, including porting one problem by hand.

```
> /phase docs/prompts/phase-4-python.md
```

Ports are batched. Repeat per problem or per group of five:

```
> Port <problem ids> to Python under content/problems/<id>/variants/python/ following the Python column of PLAN.md 4.2: starter with the same planted bug, read-only tests on the Python kit, reference, naive that times out in the last phase with Python's 6x headroom, data files. Run content/tools verify for Python on each. List anything that could not be expressed idiomatically so I can review it.
```

Human: send the ports to the native reviewer; paste their notes into the next prompt.

```
> Reviewer notes for the Python ports: <...>. Apply them, re-verify, then enable the Python flag for the tester allowlist and update docs/status.md.
```

```
> Manual results for Phase 4: <...>. Record, fix, re-run gates, confirm exit criteria, set the Python flag to 100%, tag lang-python-v1.0.0 and the app release, write docs/retro/phase-4.md.
```

---

## Phase 5: JavaScript/TypeScript and the website-practice decision

Human first: `docs/learning/phase-5.md`.

```
> /phase docs/prompts/phase-5-typescript.md
```

Same port and reviewer prompts as Phase 4 with "TypeScript" and `variants/javascript/`. Then:

```
> Run the container cost spike from the prompt: the engine with the Java and Python toolchains in a Docker image with CPU and memory caps, 20 simulated sessions, cost per session-hour at the E2E Networks and Fly.io prices in PLAN.md 9.2. Write docs/adr/00NN-website-practice.md with a go or no-go and the numbers.
```

---

## Phase 6: Go and Linux

Human first: `docs/learning/phase-6.md`.

```
> /phase docs/prompts/phase-6-go-linux.md
```

Same port and reviewer prompts with "Go" and `variants/go/`. Human: test the AppImage and deb on Ubuntu LTS and Fedora.

---

## Phase 7: DSA quick section, catalogue to 60

Human first: `docs/learning/phase-7.md`.

```
> /phase docs/prompts/phase-7-dsa-section.md
```

Then the content batches, in four languages per problem:

```
> Author DSA quick problem "<title>" for technique <t> in content/problems/<id>/ with a single phase, hints, and variants in Java, Python, TypeScript and Go, verified in each. Show me the brief before the PR.
```

---

## Phase 8: system design section

Human first: `docs/learning/phase-8.md`.

```
> /phase docs/prompts/phase-8-system-design.md
```

```
> Build the calibration set: for each of the 15 design problems, one model answer and one weak answer; run the reviewer grader on all 30 and report, per problem, whether the model answer scored higher. Fix rubrics or the grader prompt where ordering fails, then re-run.
```

---

## Phase 9: industry mode

Human first: `docs/learning/phase-9.md`, and a lawyer's review booked.

```
> /phase docs/prompts/phase-9-industry-mode.md
```

Human: demonstrate detection on test machines as the exit criteria require; paste the evidence and the lawyer's notes into the closing prompt.

---

## Follow-up prompts you will need in every phase

When the pipeline stopped on a design-review violation:

```
> Fix the plan to satisfy the design reviewer's violations without widening scope, re-run the design review, and continue the phase from step 5.
```

When a gate is red:

```
> The qa-gatekeeper reported RED on <gate>. Find the root cause, fix it in the part that owns it, re-run /simplify and /code-review on the change, then re-run the gates.
```

When Claude Code was interrupted or the session was lost:

```
> Resume the current phase. Run the status-diagnoser against docs/prompts/<current phase>.md, read docs/plans/<phase>.md and git log, tell me which pipeline step we are on, and continue from there.
```

When the plan and reality disagree:

```
> The plan says <X> but <Y> turned out to be true because <evidence>. Update docs/plan/PLAN.md consistently wherever X appears, the affected prompt in docs/prompts/, the gates in docs/process/definition-of-done.md and the learning file if needed, then list every file you touched.
```

A content hotfix:

```
> Problem <id> has <defect>. Fix it inside content/problems/<id>/ only, bump the problem version and the pack version, run content/tools verify, open the PR with the content label, and tell me when the pack is published so apps pick it up.
```

A review or simplification pass on its own:

```
> /code-review high
```

```
> /simplify
```

Adding a developer or an AI agent to the repo later:

```
> Read CONTRIBUTING.md and CLAUDE.md, then summarise in ten lines the rules a new contributor must follow before their first PR, and point them at the part they will work on.
```

End of each month:

```
> Run the monthly pass from docs/process/review.md: list flags at 100% for two releases, abstractions with one implementation, dead code, dependencies behind on security updates, and problems whose reference time has drifted past the 5x headroom rule. Propose deletions and open one PR per part.
```
