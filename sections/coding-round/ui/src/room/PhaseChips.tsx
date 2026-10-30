import type { SessionView } from '@intervue/local-engine-client';
import { clockText } from '@intervue/ui';

/** One dot per phase in the header: filled when passed, ringed when open, hollow when locked. */
export function ProgressDots({ view }: { view: SessionView }) {
  return (
    <ol className="dots" aria-label="Phase progress">
      {Array.from({ length: view.phases }, (_, i) => i + 1).map((n) => {
        const passedAt = view.phasesPassed[String(n)];
        const state = passedAt !== undefined ? 'passed' : n <= view.unlocked ? 'open' : 'locked';
        const tip = passedAt !== undefined ? `passed at ${clockText(passedAt)}` : state;
        return <li key={n} className={state} aria-label={`Phase ${n}: ${state}`} title={`Phase ${n}: ${tip}`} />;
      })}
    </ol>
  );
}
