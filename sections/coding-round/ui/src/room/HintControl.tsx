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
