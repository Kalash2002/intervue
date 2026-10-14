// The session's files as the room holds them: what is open, unsaved text, the engine's symbols and
// diagnostics after each save, and AI-edit marks per file.
import { useCallback, useEffect, useMemo, useState } from 'react';
import type { AiEditMark } from '@intervue/editor';
import type { FileEntry, SourceDiagnostic, SourceSymbol } from '@intervue/local-engine-client';
import { failed, useApp } from '@intervue/ui';

export interface Doc {
  text: string;
  saved: string;
}

/** The first editable source the candidate is likely to start in. */
function firstFile(files: readonly FileEntry[]): string | null {
  const editable = files.filter((f) => !f.readOnly && !f.path.includes('/'));
  return (editable.find((f) => !f.path.endsWith('Tests.java')) ?? editable[0] ?? files[0])?.path ?? null;
}

/** Marks what was written as saved, keeping anything typed while the write was in flight. */
function markSaved(all: Record<string, Doc>, written: [string, Doc][]): Record<string, Doc> {
  const next = { ...all };
  for (const [path, d] of written) next[path] = { text: next[path].text, saved: d.text };
  return next;
}

/** The engine's symbols and compiler diagnostics for the session, refreshed after each save. */
function useEditorTools(sessionId: string) {
  const { client, notify } = useApp();
  const [symbols, setSymbols] = useState<SourceSymbol[]>([]);
  const [diagnostics, setDiagnostics] = useState<SourceDiagnostic[]>([]);
  const refreshTools = useCallback(
    () =>
      Promise.all([client.symbols(sessionId), client.diagnostics(sessionId)])
        .then(([s, d]) => (setSymbols(s), setDiagnostics(d)))
        .catch(failed(notify)),
    [client, notify, sessionId],
  );
  return { symbols, diagnostics, setDiagnostics, refreshTools };
}

export function useWorkspace(sessionId: string) {
  const { client, notify } = useApp();
  const [files, setFiles] = useState<FileEntry[]>([]);
  const [docs, setDocs] = useState<Record<string, Doc>>({});
  const [active, setActive] = useState<string | null>(null);
  const { symbols, diagnostics, setDiagnostics, refreshTools } = useEditorTools(sessionId);
  const [marks, setMarks] = useState<Record<string, AiEditMark[]>>({});

  const load = useCallback(
    async (path: string) => {
      const { text } = await client.readFile(sessionId, path);
      setDocs((d) => ({ ...d, [path]: { text, saved: text } }));
      return text;
    },
    [client, sessionId],
  );

  useEffect(() => {
    client
      .files(sessionId)
      .then(async (list) => {
        setFiles(list);
        const first = firstFile(list);
        if (first) await load(first);
        setActive(first);
        await refreshTools();
      })
      .catch(failed(notify));
  }, [client, load, notify, refreshTools, sessionId]);

  const saveAll = useCallback(async () => {
    const dirty = Object.entries(docs).filter(([, d]) => d.text !== d.saved);
    for (const [path, d] of dirty) await client.writeFile(sessionId, path, d.text);
    setDocs((all) => markSaved(all, dirty));
    await refreshTools();
  }, [client, docs, refreshTools, sessionId]);

  return useMemo(
    () => ({
      files,
      docs,
      active,
      symbols,
      diagnostics,
      marks,
      setDiagnostics,
      setMarks,
      load,
      saveAll,
      open: (path: string) => (docs[path] ? Promise.resolve() : load(path)).then(() => setActive(path)),
      edit: (path: string, text: string) => setDocs((d) => ({ ...d, [path]: { ...d[path], text } })),
      reloadOpen: () => Promise.all(Object.keys(docs).map(load)),
    }),
    [files, docs, active, symbols, diagnostics, setDiagnostics, marks, load, saveAll],
  );
}

export type Workspace = ReturnType<typeof useWorkspace>;
