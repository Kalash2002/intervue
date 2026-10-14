# Learning for Phase 3: website dashboard, catalogue growth, authoring workbench

Time (est.): 10 hours.

## Minimal concepts

| Concept | Why it matters in Dryrun | Source |
|---|---|---|
| Read-only dashboards with TanStack Query: caching, pagination, loading and error states | The website dashboard from synced data | TanStack Query docs, "Queries" and "Paginated queries" |
| Small charts that read well: one metric per chart, no decoration | Score history and time practised | Any short guide on chart design; keep to a bar and a line |
| The chosen gateway's hosted checkout and webhooks | Replacing the mock adapter | That gateway's integration docs |
| Running verification inside a container in CI: Docker image with the JDK, cache, timeouts | The authoring workbench verifies drafts safely | Docker getting started; GitHub Actions `container` jobs |
| Prompt engineering for content drafting: a fixed authoring prompt with examples, structured output, review before acceptance | The workbench drafts problems you then verify | Anthropic prompt engineering guide: clear instructions, examples, structured outputs |

## Tutor prompt

```
You are my tutor for the third phase of Dryrun: a website dashboard that reads synced practice sessions, a payment gateway adapter replacing a mock, and an admin workbench that drafts new interview problems with an AI model and verifies them in a container before a human approves. I am a backend engineer (Java, Spring Boot) who knows React and TanStack Query basics. I have about 10 hours.

Teach me only: building a read-only dashboard with TanStack Query including pagination, loading and error states; two simple charts (score over time, minutes practised per week) that read clearly; integrating a hosted checkout and verifying its webhooks idempotently behind an existing port; running a verification job inside a Docker container in GitHub Actions with a timeout and a cached JDK; writing a fixed authoring prompt with two worked examples and a structured output schema so drafts are consistent, and how to review drafts before accepting them.

Method: two-sentence explanation, one minimal example tied to Dryrun, one common mistake, then one question to check I understood before moving on. Keep answers short. At the end give me a 30-minute exercise: draft the authoring prompt for one problem using my problem format, and a checklist of what I should be able to do without notes.

Do not teach me data warehousing, charting libraries in depth, or fine-tuning models.
```
