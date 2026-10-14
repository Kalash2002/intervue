// The app's screens, shared parts and the contract a section UI fills (PLAN.md 5.2, packages/ui).
import './styles/tokens.css';
import './styles/base.css';
import './styles/room.css';
import './styles/timeline.css';
import './styles/panel.css';
import './styles/pages.css';
import './styles/result.css';

export { App, type AppProps } from './App.tsx';
export { AppContext, errorText, failed, useApp, type AppServices, type Screen } from './app-context.ts';
export { Markdown } from './components/Markdown.tsx';
export { ConfirmButton, ThemeButton, TopBar } from './components/TopBar.tsx';
export { clockText, dateText, plural } from './lib/format.ts';
export { timed, useActiveMs, type TimedSession } from './lib/timed.ts';
export type { SectionResultProps, SectionRoomProps, SectionUi } from './section.ts';
