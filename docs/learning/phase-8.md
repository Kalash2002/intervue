# Learning for Phase 8: system design section

Time (est.): 8 hours.

## Minimal concepts

| Concept | Why it matters in Dryrun | Source |
|---|---|---|
| Rubric design: criteria that are observable, independent and weighted; evidence hints | Offline self-marking and online model grading use the same rubric | Any assessment design primer on analytic rubrics; PLAN.md 7.5 |
| Model as grader: structured JSON output validated against the rubric, temperature and effort choices, position and length bias | Reviewer-model scores that mean something | Anthropic docs on structured outputs; a short LLM-as-judge survey |
| Calibration on a test set: model answers and weak answers must order correctly in at least 14 of 15 problems | The exit criterion | PLAN.md phase 8 prompt |
| Prompt caching for the rubric and brief prefix | Cost per graded session near $0.05 | Anthropic prompt caching docs |
| Step-wise brief reveal in the UI | The interviewer-style progression | Your coding-round phase unlock as the model |

## Tutor prompt

```
You are my tutor for building a rubric-graded, no-code interview section with an AI reviewer. I am a backend engineer (Java, Spring Boot) with React, building Dryrun. The new section gives a system design prompt step by step, the user answers in text and a simple diagram, offline they self-mark a rubric after reading a model answer, and online a model returns rubric scores as validated JSON. I have about 8 hours.

Teach me only: writing analytic rubrics whose criteria are observable, independent and weighted, with evidence hints; using a language model as a grader with a strict JSON output schema validated against the rubric, the biases to control (answer length, position, verbosity) and the settings that reduce variance; building a calibration set of model and weak answers and checking that scores order correctly; placing the rubric and brief in a cached prompt prefix so grading stays cheap; showing offline self-assessed and online model scores side by side without confusing the user.

Method: two-sentence explanation, one minimal example tied to Dryrun (a URL shortener design problem with a five-criterion rubric), one common mistake, then one question to check I understood before moving on. Keep answers short. At the end give me a 30-minute exercise: write that rubric as JSON and the grader prompt, and a checklist of what I should be able to do without notes.

Do not teach me system design content itself, fine-tuning, or embeddings.
```
