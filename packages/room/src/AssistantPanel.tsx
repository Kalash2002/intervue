import { useEffect, useState } from 'react';
import type { EditReply, RulesChoice, SessionView } from '@intervue/local-engine-client';
import { failed, Markdown, useApp } from '@intervue/ui';
import { useChat, type ShownTurn } from './useChat.ts';
import type { RoomActions } from './useRoomActions.ts';

/**
 * The assistant (PLAN.md 2.8): Ask streams a text reply; Write code applies edits through the
 * engine and offers an Undo per applied edit. Interview sessions always use point-and-explain
 * rules; Practice may switch. When no route can answer, the panel shows why instead of controls.
 */
export function AssistantPanel({
  view,
  actions,
  canWrite,
  onChange,
}: {
  view: SessionView;
  actions: RoomActions;
  /** Whether the section has the WRITE_CODE channel; callers pass view.channels.includes. */
  canWrite: boolean;
  onChange: (v: SessionView) => void;
}) {
  const { client } = useApp();
  const chat = useChat(view.id, () => void client.session(view.id).then(onChange));
  const { status } = chat;
  if (!status) return <p className="hint">Checking the assistant…</p>;
  return (
    <>
      {status.available ? (
        <>
          <ChatLog turns={chat.turns} streaming={chat.busy} />
          <Composer view={view} chat={chat} actions={actions} canWrite={canWrite} />
          <Outcomes reply={actions.reply} onUndo={actions.undo} />
        </>
      ) : (
        <p className="assistant-off">{status.reason}</p>
      )}
      {status.developer && <Demo finished={view.finished} actions={actions} />}
    </>
  );
}

/** The reply still streaming shows as plain text; Markdown renders once it is complete. */
function ChatLog({ turns, streaming }: { turns: ShownTurn[]; streaming: boolean }) {
  if (turns.length === 0) {
    return <p className="hint">Say what you want and why. Name the file or method when you want code.</p>;
  }
  return (
    <div className="chat-log" aria-label="Chat">
      {turns.map((t, i) => (
        <div key={i} className={`msg ${t.role}${t.error ? ' error' : ''}`}>
          <div className="who">{t.role === 'user' ? 'You' : 'Assistant'}</div>
          {t.role === 'user' || t.error || (streaming && i === turns.length - 1) ? (
            <div className="body">{t.text || '…'}</div>
          ) : (
            <Markdown source={t.text} />
          )}
        </div>
      ))}
    </div>
  );
}

function RulesSelect({ rules, onChange }: { rules: RulesChoice; onChange: (r: RulesChoice) => void }) {
  return (
    <label className="chat-options">
      Rules
      <select aria-label="Rules" value={rules} onChange={(e) => onChange(e.target.value as RulesChoice)}>
        <option value="helpful">Helpful</option>
        <option value="interview">Interview rules</option>
      </select>
    </label>
  );
}

function Composer({
  view,
  chat,
  actions,
  canWrite,
}: {
  view: SessionView;
  chat: ReturnType<typeof useChat>;
  actions: RoomActions;
  canWrite: boolean;
}) {
  const [message, setMessage] = useState('');
  const [rules, setRules] = useState<RulesChoice>('helpful');
  // Interview sessions always get point-and-explain rules (PLAN.md 2.8); the engine enforces it too.
  const effective: RulesChoice = view.type === 'INTERVIEW' ? 'interview' : rules;
  const writes = view.events.filter((e) => e.type === 'aiCall' && e.data.kind === 'edit').length;
  const disabled = view.finished || chat.busy || actions.busy || message.trim() === '';
  const submit = (kind: 'ask' | 'write') => {
    const text = message.trim();
    setMessage('');
    return kind === 'ask'
      ? chat.ask(text, effective, actions.saveFiles)
      : chat.write(text, () => actions.writeCode(text, effective));
  };
  return (
    <div className="chat-form">
      {view.type === 'PRACTICE' && <RulesSelect rules={rules} onChange={setRules} />}
      <textarea
        className="chat-input"
        aria-label="Message"
        value={message}
        placeholder="Ask about the code, or state your approach and name what to write."
        onChange={(e) => setMessage(e.target.value)}
      />
      <div className="chat-send">
        <span className="hint">{canWrite ? `Write code calls this session: ${writes}` : ''}</span>
        <span>
          <button className="ghost small" type="button" disabled={disabled} onClick={() => submit('ask')}>
            Ask
          </button>
          {canWrite && (
            <>
              {' '}
              <button className="primary small" type="button" disabled={disabled} onClick={() => submit('write')}>
                Write code
              </button>
            </>
          )}
        </span>
      </div>
    </div>
  );
}

function Outcomes({ reply, onUndo }: { reply: EditReply | null; onUndo: (id: string) => void }) {
  if (!reply || reply.outcomes.length === 0) return null;
  return (
    <ul className="outcomes" aria-label="Last edits">
      {reply.outcomes.map((o) => (
        <li key={o.id}>
          <b>{o.status.replace('_', ' ').toLowerCase()}</b> {o.op} <code>{o.path}</code> {o.message}
          {o.status === 'APPLIED' && (
            <button className="ghost small" type="button" aria-label={`Undo ${o.id}`} onClick={() => onUndo(o.id)}>
              Undo
            </button>
          )}
        </li>
      ))}
    </ul>
  );
}

/** Developer mode only: a recorded Write code reply, so edits and Undo can be tried without a model. */
function Demo({ finished, actions }: { finished: boolean; actions: RoomActions }) {
  const { client, notify } = useApp();
  const [streams, setStreams] = useState<string[]>([]);
  const [stream, setStream] = useState('');
  useEffect(() => {
    client
      .fakeEditIds()
      .then((ids) => (setStreams(ids), setStream(ids[0] ?? '')))
      .catch(failed(notify));
  }, [client, notify]);
  return (
    <details className="demo">
      <summary>Developer: replay a recorded reply</summary>
      <select aria-label="Recorded reply" value={stream} onChange={(e) => setStream(e.target.value)}>
        {streams.map((s) => (
          <option key={s}>{s}</option>
        ))}
      </select>{' '}
      <button
        className="ghost small"
        type="button"
        disabled={finished || actions.busy || !stream}
        onClick={() => actions.replay(stream)}
      >
        Replay
      </button>
    </details>
  );
}
