import { useState, type ReactNode } from 'react';

type Theme = 'dark' | 'light';

function applyTheme(theme: Theme) {
  document.documentElement.dataset.theme = theme;
  localStorage.setItem('intervue.theme', theme);
}

export function ThemeButton() {
  const [theme, setTheme] = useState<Theme>(() =>
    localStorage.getItem('intervue.theme') === 'light' ? 'light' : 'dark',
  );
  const next: Theme = theme === 'dark' ? 'light' : 'dark';
  return (
    <button
      className="ghost"
      type="button"
      onClick={() => {
        applyTheme(next);
        setTheme(next);
      }}
    >
      {next === 'light' ? 'Light theme' : 'Dark theme'}
    </button>
  );
}

/** The bar on top of the room and the result page: wordmark home button, then the caller's parts. */
export function TopBar({ onHome, children }: { onHome: () => void; children: ReactNode }) {
  return (
    <header className="topbar">
      <button className="ghost back" type="button" title="Back to the dashboard" onClick={onHome}>
        Intervue
      </button>
      {children}
      <ThemeButton />
    </header>
  );
}

/** A button that asks for a second click instead of a modal dialog. */
export function ConfirmButton({
  label,
  confirmLabel,
  onConfirm,
  className = 'primary',
  disabled = false,
}: {
  label: string;
  confirmLabel: string;
  onConfirm: () => void;
  className?: string;
  disabled?: boolean;
}) {
  const [armed, setArmed] = useState(false);
  return (
    <button
      className={className}
      type="button"
      disabled={disabled}
      onBlur={() => setArmed(false)}
      onClick={() => {
        if (armed) onConfirm();
        setArmed(!armed);
      }}
    >
      {armed ? confirmLabel : label}
    </button>
  );
}
