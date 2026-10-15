// One "continue" card on Home: an open session to resume, or a section's next problem to start.
import type { ReactNode } from 'react';

export function ContinueCard({
  eyebrow,
  title,
  detail,
  action,
  onAction,
}: {
  eyebrow: string;
  title: ReactNode;
  detail: string;
  action: string;
  onAction: () => void;
}) {
  return (
    <article className="card continue">
      <p className="eyebrow">{eyebrow}</p>
      <h3>{title}</h3>
      <p className="summary">{detail}</p>
      <div className="actions">
        <button className="primary" type="button" onClick={onAction}>
          {action} <span aria-hidden="true">→</span>
        </button>
      </div>
    </article>
  );
}
