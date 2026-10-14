// The chat log of a session: earlier turns from the engine, plus the reply streaming in now.
import { useEffect, useState } from 'react';
import type { AiStatus, ChatTurn, RulesChoice } from '@intervue/local-engine-client';
import { errorText, failed, useApp } from '@intervue/ui';

export interface ShownTurn {
  role: 'user' | 'assistant';
  text: string;
  error?: boolean;
}

export function useChat(sessionId: string, onDone: () => void) {
  const { client, notify } = useApp();
  const [status, setStatus] = useState<AiStatus | null>(null);
  const [turns, setTurns] = useState<ShownTurn[]>([]);
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    Promise.all([client.aiStatus(), client.chat(sessionId)])
      .then(([s, history]) => {
        setStatus(s);
        setTurns(history.map((t: ChatTurn) => ({ role: t.role, text: t.text })));
      })
      .catch(failed(notify));
  }, [client, notify, sessionId]);

  /**
   * Shows the message at once, then the reply as it streams; a failure shows its reason inline.
   * Write code already reloads the session after applying, so it refreshes here only on failure.
   */
  const send = async (message: string, call: (onDelta: (text: string) => void) => Promise<unknown>, refresh = true) => {
    setBusy(true);
    setTurns((t) => [...t, { role: 'user', text: message }, { role: 'assistant', text: '' }]);
    const replaceLast = (update: (last: ShownTurn) => ShownTurn) =>
      setTurns((t) => [...t.slice(0, -1), update(t[t.length - 1])]);
    try {
      await call((d) => replaceLast((last) => ({ ...last, text: last.text + d })));
    } catch (error) {
      replaceLast(() => ({
        role: 'assistant',
        text: errorText(error),
        error: true,
      }));
      refresh = true;
    } finally {
      setBusy(false);
      if (refresh) onDone();
    }
  };

  return {
    status,
    turns,
    busy,
    ask: (message: string, rules: RulesChoice) =>
      send(message, (onDelta) => client.ask(sessionId, message, rules, onDelta)),
    /** Write code shows its note as the reply; the outcomes come from the room's reply state. */
    write: (message: string, run: () => Promise<{ note: string } | null>) =>
      send(
        message,
        async (onDelta) => {
          const reply = await run();
          onDelta(reply?.note || 'No changes.');
        },
        false,
      ),
  };
}
