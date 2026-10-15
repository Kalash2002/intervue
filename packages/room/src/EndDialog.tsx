// The "End the session?" dialog both rooms open from their Finish button.
import type { ReactNode } from 'react';

/** `onEarly` adds the "End early" button for rooms with phases that can be left open. */
export function EndDialog({
  children,
  onCancel,
  onEarly,
  onDone,
}: {
  children: ReactNode;
  onCancel: () => void;
  onEarly?: () => void;
  onDone: () => void;
}) {
  return (
    <div className="modal">
      <div className="modal-box" role="dialog" aria-modal="true" aria-label="End the session?">
        <h3>End the session?</h3>
        <p>{children}</p>
        <div className="modal-actions">
          <button className="ghost" type="button" onClick={onCancel}>
            Cancel
          </button>
          {onEarly && (
            <button className="ghost" type="button" onClick={onEarly}>
              End early
            </button>
          )}
          <button className="primary" type="button" onClick={onDone}>
            I&apos;m done
          </button>
        </div>
      </div>
    </div>
  );
}
