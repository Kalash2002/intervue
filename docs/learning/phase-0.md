# Learning for Phase 0: validation

Time (est.): 6 hours over the two weeks of section 13.

## Minimal concepts

| Concept | Why it matters in Dryrun | Source |
|---|---|---|
| Non-leading user interviews | The first 15 problems and the price come from what engineers say, not from what you hope | "The Mom Test" (Fitzpatrick), chapters 1 to 3 |
| Pricing survey with three price points | ₹1,500 monthly and ₹3,500 quarterly are a hypothesis until a stranger reacts | Van Westendorp price sensitivity, the Wikipedia summary is enough |
| Questions for a chartered accountant | GST threshold, export of services, selling through a merchant of record, income tax on USD receipts | PLAN.md section 8.4 and `docs/plan/research/payments.md` |
| Apple Developer Program enrolment from India and an individual code-signing certificate | Both take days to weeks and sit on the Phase 1c path | PLAN.md section 3.2; Apple's purchase and activation page; your CA's identity-validation steps |
| GitHub repository protection | `main` protected with required CI from the first commit | GitHub docs on branch protection rules |

## Tutor prompt

```
You are my tutor for product validation. I am a backend engineer (Java, Spring Boot, about two years) in India, building Dryrun, a desktop app for practising AI-assisted coding interviews, part-time at 8 to 20 hours a week. I have about 6 hours for this topic.

Teach me only what I need to run Phase 0 of my plan: (1) running ten 20-minute user interviews with Indian working engineers and students without leading them, (2) a three-price-point pricing survey for a monthly plan at ₹1,500 and a quarterly plan at ₹3,500, (3) the questions to bring to a chartered accountant about GST registration thresholds for services, export of services, selling through a foreign merchant of record, and USD receipts, (4) what to expect when enrolling in the Apple Developer Program from India and buying an individual-validation code-signing certificate, (5) protecting a GitHub main branch with required CI.

Method: for each item, a two-sentence explanation, one concrete example tied to Dryrun, one common mistake, then one question to check I understood before moving on. Keep answers short. When I am wrong, correct me with the reason. At the end give me a 30-minute exercise that produces my interview script and my CA question list, and a checklist of what I should be able to do without notes.

Do not teach me marketing funnels, fundraising or company formation. If something depends on a current price or policy, say so and tell me to check the official page.
```
