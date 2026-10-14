import { useEffect, useState } from 'react';
import type { SessionView } from '@intervue/local-engine-client';
import { failed, Markdown, useApp } from '@intervue/ui';

const LEVELS = 3;

/** Reveals one authored level at a time, in order; each reveal is a `hint` event on the timeline. */
export function HintControl({
  sessionId,
  phase,
  used,
  disabled = false,
  onRevealed,
}: {
  sessionId: string;
  phase: number;
  used: number;
  disabled?: boolean;
  onRevealed: (view: SessionView) => void;
}) {
  const { client, notify } = useApp();
  const [texts, setTexts] = useState<string[]>([]);

  useEffect(() => {
    if (used > 0) client.revealedHints(sessionId, phase).then(setTexts).catch(failed(notify));
  }, [client, notify, phase, sessionId, used]);

  const reveal = () =>
    client
      .revealHint(sessionId, phase)
      .then(async (hint) => {
        setTexts((t) => [...t, hint.text]);
        onRevealed(await client.session(sessionId));
      })
      .catch(failed(notify));

  const next = texts.length + 1;
  return (
    <div>
      {texts.map((text, i) => (
        <div className="hint-level" key={i}>
          <p className="hint">Level {i + 1}</p>
          <Markdown source={text} />
        </div>
      ))}
      {texts.length > 0 && <Explain sessionId={sessionId} phase={phase} onDone={onRevealed} />}
      {next <= LEVELS ? (
        <button className="ghost small" type="button" disabled={disabled} onClick={reveal}>
          Show hint level {next} of {LEVELS}
        </button>
      ) : (
        <p className="hint">All three levels shown.</p>
      )}
    </div>
  );
}

/**
 * Practice only (PLAN.md 2.8): the assistant explains the highest revealed level, going no further;
 * the reply streams in under the hints. When no route can answer, its reason shows instead.
 */
function Explain({
  sessionId,
  phase,
  onDone,
}: {
  sessionId: string;
  phase: number;
  onDone: (view: SessionView) => void;
}) {
  const { client } = useApp();
  const [text, setText] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);
  const explain = async () => {
    setBusy(true);
    setText('');
    setError(null);
    try {
      await client.explainHint(sessionId, phase, (d) => setText((t) => t + d));
      onDone(await client.session(sessionId));
    } catch (e) {
      setError(e instanceof Error ? e.message : String(e));
    } finally {
      setBusy(false);
    }
  };
  return (
    <div className="hint-explain">
      <button className="ghost small" type="button" disabled={busy} onClick={explain}>
        Explain this hint
      </button>
      {error && <p className="assistant-off">{error}</p>}
      {text && <Markdown source={text} />}
    </div>
  );
}
