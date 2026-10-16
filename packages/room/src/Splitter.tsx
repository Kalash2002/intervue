// Drag handles between the room's panes, as in the prototype: dragging sets a size variable that the
// room stylesheet reads (--files-w, --right-w, --out-h). CodeMirror follows its own resizes.
import type { PointerEvent as ReactPointerEvent } from 'react';

const clamp = (value: number, low: number, high: number) => Math.max(low, Math.min(value, Math.max(low, high)));

/** The new size in px for a pointer at (x, y), given the work area and its center column. */
export const SIZES = {
  '--files-w': (x: number, _y: number, work: DOMRect) => clamp(x - work.left, 140, 420),
  '--statement-w': (x: number, _y: number, work: DOMRect) => clamp(x - work.left, 300, 640),
  '--right-w': (x: number) => clamp(window.innerWidth - x, 300, window.innerWidth - 520),
  '--out-h': (_x: number, y: number, _work: DOMRect, center: DOMRect) =>
    clamp(center.bottom - y, 80, center.height - 140),
} as const;

export function Splitter({ size, label }: { size: keyof typeof SIZES; label: string }) {
  const start = (e: ReactPointerEvent<HTMLDivElement>) => {
    e.preventDefault();
    const handle = e.currentTarget;
    const work = handle.closest<HTMLElement>('.work');
    if (!work) return;
    // Capture keeps the drag alive when the pointer crosses the editor or leaves the handle.
    handle.setPointerCapture?.(e.pointerId);
    const move = (ev: PointerEvent) => {
      const center = work.querySelector('.center')?.getBoundingClientRect() ?? new DOMRect();
      const px = SIZES[size](ev.clientX, ev.clientY, work.getBoundingClientRect(), center);
      work.style.setProperty(size, `${Math.round(px)}px`);
    };
    const up = () => {
      handle.removeEventListener('pointermove', move);
      handle.removeEventListener('pointerup', up);
    };
    handle.addEventListener('pointermove', move);
    handle.addEventListener('pointerup', up);
  };
  const vertical = size !== '--out-h';
  return (
    <div
      className={vertical ? 'vsplit' : 'hsplit'}
      role="separator"
      aria-orientation={vertical ? 'vertical' : 'horizontal'}
      aria-label={label}
      title="Drag to resize"
      onPointerDown={start}
    />
  );
}
