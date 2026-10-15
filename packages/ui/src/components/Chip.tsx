// A chip: a small pill button for tags and filters. `on` makes it a toggle with aria-pressed;
// without it the chip is a plain navigation button.
import type { ReactNode } from 'react';

export function Chip({ on, onClick, children }: { on?: boolean; onClick: () => void; children: ReactNode }) {
  return (
    <button type="button" className={on ? 'chip on' : 'chip'} aria-pressed={on} onClick={onClick}>
      {children}
    </button>
  );
}
