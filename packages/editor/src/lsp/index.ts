// Tier 2 (PLAN.md 4.5): a language server bridged to @codemirror/lsp-client. Phase 2 fills this in;
// until then the editor stays on tier 1 and this reports that no server is available.
import type { Extension } from '@codemirror/state';

export function languageServer(): Extension | null {
  return null;
}
