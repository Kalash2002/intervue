import { useEffect, useState } from 'react';
import { failed, useApp } from '@intervue/ui';
import type { RoomActions } from './useRoomActions.ts';

const LATER = 'Available after sign-in in a later phase';

/**
 * Ask and Write code arrive with sign-in (Phase 1b). Until then a recorded Write code reply bundled
 * with the engine can be replayed, so edits, highlights and Undo work end to end.
 */
export function AssistantPanel({ finished, actions }: { finished: boolean; actions: RoomActions }) {
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
    <>
      <div className="assistant-off">
        <p>{LATER}.</p>
        <button className="ghost small" type="button" disabled title={LATER}>
          Ask
        </button>
        <button className="ghost small" type="button" disabled title={LATER}>
          Write code
        </button>
        <textarea className="chat-input" disabled placeholder={LATER} />
      </div>
      <h3>Demo: a recorded Write code reply</h3>
      <p className="hint">
        Applies a recorded reply through the same rules as a real one. Applied edits are highlighted with an Undo
        button, and a run afterwards marks the session assisted.
      </p>
      <select aria-label="Recorded reply" value={stream} onChange={(e) => setStream(e.target.value)}>
        {streams.map((s) => (
          <option key={s}>{s}</option>
        ))}
      </select>{' '}
      <button
        className="primary small"
        type="button"
        disabled={finished || actions.busy || !stream}
        onClick={() => actions.replay(stream)}
      >
        Replay
      </button>
      {actions.reply && (
        <>
          <p>{actions.reply.note}</p>
          <ul className="outcomes">
            {actions.reply.outcomes.map((o) => (
              <li key={o.id}>
                <b>{o.status.replace('_', ' ').toLowerCase()}</b> {o.op} <code>{o.path}</code> {o.message}
              </li>
            ))}
          </ul>
        </>
      )}
    </>
  );
}
