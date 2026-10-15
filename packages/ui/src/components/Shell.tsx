// The frame around every screen but the room: a fixed left menu (Home, the Practice group with one
// entry per registered section, Doctor and the theme switch) beside the main column. It owns the
// collapsed state only; what the main column shows is App's business.
import { useState, type ReactNode } from 'react';
import { useApp, type Screen } from '../app-context.ts';
import { ThemeButton } from './TopBar.tsx';

const COLLAPSED_KEY = 'intervue.menu';

export function Shell({ screen, children }: { screen: Screen; children: ReactNode }) {
  const { go, sections } = useApp();
  const [collapsed, setCollapsed] = useState(() => localStorage.getItem(COLLAPSED_KEY) === 'collapsed');
  const toggle = () => {
    setCollapsed(!collapsed);
    localStorage.setItem(COLLAPSED_KEY, collapsed ? 'open' : 'collapsed');
  };
  const activeSection = screen.name === 'section' ? screen.sectionId : null;
  return (
    <div className={collapsed ? 'shell collapsed' : 'shell'}>
      <nav className="menu" aria-label="Main menu">
        <div className="menu-top">
          <span className="wordmark">intervue</span>
          <button
            className="icon menu-toggle"
            type="button"
            aria-expanded={!collapsed}
            aria-label={collapsed ? 'Expand menu' : 'Collapse menu'}
            onClick={toggle}
          >
            {collapsed ? '»' : '«'}
          </button>
        </div>
        <MenuItem
          glyph="⌂"
          label="Home"
          active={screen.name === 'dashboard'}
          onClick={() => go({ name: 'dashboard' })}
        />
        <div className="menu-group">Practice</div>
        {sections.map((s) => (
          <MenuItem
            key={s.id}
            glyph={s.title.charAt(0)}
            label={s.title}
            active={activeSection === s.id}
            onClick={() => go({ name: 'section', sectionId: s.id })}
          />
        ))}
        <div className="menu-foot">
          <MenuItem
            glyph="✚"
            label="Doctor"
            active={screen.name === 'doctor'}
            onClick={() => go({ name: 'doctor', firstRun: false })}
          />
          <ThemeButton menu />
        </div>
      </nav>
      <div className="main">{children}</div>
    </div>
  );
}

function MenuItem({
  glyph,
  label,
  active,
  onClick,
}: {
  glyph: string;
  label: string;
  active: boolean;
  onClick: () => void;
}) {
  return (
    <button
      type="button"
      className={active ? 'menu-item active' : 'menu-item'}
      aria-current={active ? 'page' : undefined}
      aria-label={label}
      title={label}
      onClick={onClick}
    >
      <span className="glyph" aria-hidden="true">
        {glyph}
      </span>
      <span className="label">{label}</span>
    </button>
  );
}
