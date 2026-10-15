import { useState, type ReactNode } from 'react';

type Theme = 'dark' | 'light';

/** The theme saved by the last switch; dark when none was saved. */
export function storedTheme(): Theme {
  return localStorage.getItem('intervue.theme') === 'light' ? 'light' : 'dark';
}

export function applyTheme(theme: Theme) {
  document.documentElement.dataset.theme = theme;
  localStorage.setItem('intervue.theme', theme);
}

/** `menu` draws it as a menu item with a glyph; otherwise a ghost button for the room's top bar. */
export function ThemeButton({ menu = false }: { menu?: boolean }) {
  const [theme, setTheme] = useState<Theme>(storedTheme);
  const next: Theme = theme === 'dark' ? 'light' : 'dark';
  const label = next === 'light' ? 'Light theme' : 'Dark theme';
  return (
    <button
      className={menu ? 'menu-item' : 'ghost'}
      type="button"
      aria-label={label}
      title={label}
      onClick={() => {
        applyTheme(next);
        setTheme(next);
      }}
    >
      {menu && (
        <span className="glyph" aria-hidden="true">
          {next === 'light' ? '☀' : '☾'}
        </span>
      )}
      <span className="menu-label">{label}</span>
    </button>
  );
}

/** The bar on top of the room and the result pages: a Dashboard button, then the caller's parts. */
export function TopBar({ onHome, children }: { onHome: () => void; children: ReactNode }) {
  return (
    <header className="topbar">
      <button className="ghost back" type="button" onClick={onHome}>
        ← Dashboard
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
