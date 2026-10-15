// The coding-round room (PLAN.md 2.8): file tree, editor, output, brief with hints, the assistant
// panel, phase chips, the timeline strip and the clock, laid out as in the prototype.
import { useState } from 'react';
import { languageFor } from '@intervue/editor';
import type { SessionView } from '@intervue/local-engine-client';
import { ConfirmButton, TopBar, timed, useActiveMs, useApp, type SectionRoomProps } from '@intervue/ui';
import { TimelineStrip } from '../timeline/TimelineStrip.tsx';
import { CodeEditor } from './CodeEditor.tsx';
import { FileTree } from './FileTree.tsx';
import { OutputPane } from './OutputPane.tsx';
import { PhaseChips, RoomClock } from './PhaseChips.tsx';
import { SidePanel } from './SidePanel.tsx';
import { Splitter } from './Splitter.tsx';
import { useRoomActions } from './useRoomActions.ts';
import { useWorkspace, type Workspace } from './useWorkspace.ts';

export function SessionRoom({ session: initial }: SectionRoomProps) {
  const { go } = useApp();
  const [session, setSession] = useState(initial);
  const view = session.view;
  const refresh = (v: SessionView) => setSession(timed(v));
  const ws = useWorkspace(view.id);
  const actions = useRoomActions(view, ws, refresh);
  const activeMs = useActiveMs(session);
  const [target, setTarget] = useState('phase:1');

  return (
    <div className="room">
      <TopBar onHome={() => go({ name: 'dashboard' })}>
        <div className="title">{view.title}</div>
        <PhaseChips view={view} />
        <div className="spacer" />
        <RoomClock view={view} activeMs={activeMs} />
        {view.finished ? (
          <button className="primary" type="button" onClick={() => go({ name: 'result', session: view })}>
            Result
          </button>
        ) : (
          <ConfirmButton label="Finish" confirmLabel="Finish now?" onConfirm={actions.finish} />
        )}
      </TopBar>
      <div className="strip" aria-label="Session timeline">
        <TimelineStrip view={view} activeMs={activeMs} />
      </div>
      <div className="work">
        <FileTree
          ws={ws}
          finished={view.finished}
          onResetFile={actions.resetFile}
          onResetProblem={actions.resetProblem}
        />
        <Splitter size="--files-w" label="Resize the file tree" />
        <section className="center">
          <EditorArea
            ws={ws}
            finished={view.finished}
            onUndo={actions.undo}
            onSave={actions.save}
            onRun={() => actions.run(target)}
          />
          <Splitter size="--out-h" label="Resize the output pane" />
          <OutputPane view={view} target={target} onTarget={setTarget} actions={actions} />
        </section>
        <Splitter size="--right-w" label="Resize the side panel" />
        <SidePanel view={view} actions={actions} onChange={refresh} />
      </div>
    </div>
  );
}

function EditorArea(props: {
  ws: Workspace;
  finished: boolean;
  onUndo: (id: string) => void;
  onSave: () => void;
  onRun: () => void;
}) {
  const { languages } = useApp();
  const { ws, finished } = props;
  const path = ws.active;
  const doc = path ? ws.docs[path] : undefined;
  if (!path || !doc)
    return (
      <div className="editor-wrap">
        <div className="empty">Pick a file on the left to start reading.</div>
      </div>
    );
  const readOnly = finished || (ws.files.find((f) => f.path === path)?.readOnly ?? false);
  return (
    <CodeEditor
      key={path}
      path={path}
      text={doc.text}
      readOnly={readOnly}
      language={languageFor(languages, path)}
      symbols={ws.symbols}
      diagnostics={ws.diagnostics}
      marks={ws.marks[path] ?? []}
      onChange={(text) => ws.edit(path, text)}
      onUndo={props.onUndo}
      onSave={props.onSave}
      onRun={props.onRun}
    />
  );
}
