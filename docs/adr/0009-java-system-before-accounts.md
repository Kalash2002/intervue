# 0009: the complete Java system before accounts, payment and other languages

Date: 2026-10-16. Status: accepted.

## Context

ADR 0007 put Java and Windows first and moved the DSA section and the catalogue ahead of accounts. The owner's hand test of the DSA section (2026-10-16) showed what still stands between a candidate and a complete Java practice room: no IntelliSense (an unimported `Map` fails to compile and the editor's completion does not add the import), DSA questions with the assistant where only hints belong, statements and answers not in the shape candidates know from NeetCode and LeetCode. Meanwhile the signing certificate and the AI API key that Phases 1b and 1c need are still not available, and the other languages (Phases 6 to 8 after ADR 0007) are explicitly last.

## Decision

The Java system is completed before anything that needs the certificate, the key or a backend: after 1ae come Java IntelliSense (1af, the Eclipse JDT language server plugin pulled forward from Phase 2), the catalogue to 60 (Phase 2) and the system design section (Phase 3). Accounts, packs, the payment page and the AI gateway become Phase 4, website, installers and launch Phase 5, sync and deferred feedback Phase 6, the website dashboard Phase 7. Python, TypeScript, Go and Linux are Phases 8 to 10, industry mode 11. Until Phase 4 the assistant keeps running on the developer route only.

## Consequences

- PLAN.md section 10 and `docs/prompts/README.md` carry the new order; prompt files keep their original names.
- Phase 2's prompt loses the language-server item, which is `phase-1af-java-intellisense.md`.
- Phases 2 and 3 (content and a no-code section) touch no backend, so they can run while the certificate and the key are arranged.
- The free problems and the developer-route assistant are the whole product until Phase 4; the economics in section 9 are unchanged, only later.
