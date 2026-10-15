## Phase 1: fix the bug

Dedupe removes copies that differ only in spaces or a "Remastered" tag, but **copies that differ only in capital letters survive**.

Run **Phase 1 tests**. Some fail.

- Find the cause and fix it. Keep the change small.
- Be ready to explain why the code compiled and ran without any error.
