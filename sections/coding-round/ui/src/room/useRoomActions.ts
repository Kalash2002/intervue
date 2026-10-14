// What the room's buttons do. Every action saves first where the engine reads files, then refreshes
// the session view so the clock, timeline, chips and brief follow the engine's record.
import { useState } from 'react';
import { changedRange, type AiEditMark } from '@intervue/editor';
import type { EditReply, EngineClient, SessionView } from '@intervue/local-engine-client';
import { clockText, failed, useApp } from '@intervue/ui';
import type { Workspace } from './useWorkspace.ts';

export interface OutputBlock {
  header: string;
  text: string;
}

export function useRoomActions(view: SessionView, ws: Workspace, refresh: (v: SessionView) => void) {
  const { client, go, notify } = useApp();
  const [busy, setBusy] = useState(false);
  const [output, setOutput] = useState<OutputBlock[]>([]);
  const [reply, setReply] = useState<EditReply | null>(null);
  const id = view.id;

  const guarded = (action: () => Promise<void>) => () => {
    if (busy) return;
    setBusy(true);
    action()
      .catch(failed(notify))
      .finally(() => setBusy(false));
  };
  const reload = () => client.session(id).then(refresh);

  const run = (target: string) =>
    guarded(async () => {
      await ws.saveAll(false);
      const r = await client.run(id, target);
      setOutput((o) => [...o, { header: `${target} at ${clockText(view.activeMs)}`, text: r.output }]);
      ws.setDiagnostics(r.diagnostics);
      await reload();
      const passed = r.newlyPassed.at(-1);
      if (passed !== undefined) notify(`Phase ${passed} passed.`, 'good');
    })();

  const edits = { client, id, ws, reload, setOutput, setReply };
  const replay = (streamId: string) => guarded(() => replayEdits(edits, streamId))();
  const undo = (editId: string) => guarded(() => undoEdit(edits, editId))();

  return {
    busy,
    output,
    reply,
    clearOutput: () => setOutput([]),
    run,
    replay,
    undo,
    save: guarded(() => ws.saveAll()),
    skip: guarded(() => client.skip(id).then(refresh)),
    finish: guarded(() =>
      ws.saveAll(false).then(() => client.finish(id).then((v) => go({ name: 'result', session: v }))),
    ),
    resetFile: (path: string) =>
      guarded(async () => {
        await client.resetFile(id, path);
        ws.setMarks((m) => ({ ...m, [path]: [] }));
        await ws.load(path);
        await reload();
      })(),
    resetProblem: guarded(async () => {
      await client.resetProblem(id);
      ws.setMarks({});
      await ws.resync();
      await reload();
    }),
  };
}

export type RoomActions = ReturnType<typeof useRoomActions>;

interface EditContext {
  client: EngineClient;
  id: string;
  ws: Workspace;
  reload: () => Promise<void>;
  setOutput: (update: (o: OutputBlock[]) => OutputBlock[]) => void;
  setReply: (reply: EditReply) => void;
}

/** Applies a recorded reply, then marks each applied edit on the lines it changed. */
async function replayEdits({ client, id, ws, reload, setReply }: EditContext, streamId: string) {
  await ws.saveAll(false);
  const editable = ws.files.filter((f) => !f.readOnly).map((f) => f.path);
  const before = Object.fromEntries(await Promise.all(editable.map(async (p) => [p, await ws.load(p)] as const)));
  const result = await client.replayFakeEdits(id, streamId);
  setReply(result);
  await ws.resync();
  const marks: Record<string, AiEditMark[]> = {};
  for (const o of result.outcomes.filter((x) => x.status === 'APPLIED')) {
    // ponytail: one changed block per file, shared by its edits; per-hunk ranges if users ask.
    const range = changedRange(before[o.path] ?? '', await ws.load(o.path));
    if (range) (marks[o.path] ??= []).push({ editId: o.id, ...range });
  }
  ws.setMarks((m) => ({ ...m, ...marks }));
  await reload();
}

/** A refused undo (the file changed since) shows the engine's diff instead of touching the file. */
async function undoEdit({ client, id, ws, reload, setOutput }: EditContext, editId: string) {
  // Undo re-reads files from disk, so unsaved typing is written first or it would be lost.
  await ws.saveAll(false);
  const result = await client.undoEdit(id, editId);
  if (!result.undone) {
    setOutput((o) => [...o, { header: 'Not undone: the file changed after this edit', text: result.diff ?? '' }]);
    return;
  }
  ws.setMarks((m) => Object.fromEntries(Object.entries(m).map(([p, l]) => [p, l.filter((x) => x.editId !== editId)])));
  await ws.resync();
  await reload();
}
