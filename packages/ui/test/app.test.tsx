import { fireEvent, screen, waitFor, within } from '@testing-library/react';
import type { SessionView } from '@intervue/local-engine-client';
import { describe, expect, it, vi } from 'vitest';
import { check, fakeClient, renderApp, session, summary } from './fixtures.tsx';

describe('theme', () => {
  it('applies the saved theme when the app starts', async () => {
    localStorage.setItem('intervue.theme', 'light');
    renderApp(fakeClient());

    await screen.findByRole('button', { name: 'Dark theme' });
    expect(document.documentElement.dataset.theme).toBe('light');
    localStorage.clear();
    delete document.documentElement.dataset.theme;
  });
});

describe('shell', () => {
  it('shows the menu with Home, the section under Practice, Doctor and the theme on every page but the room', async () => {
    const client = fakeClient();
    renderApp(client);

    const menu = await screen.findByRole('navigation', { name: 'Main menu' });
    expect(within(menu).getByRole('button', { name: 'Home' })).toHaveAttribute('aria-current', 'page');
    expect(within(menu).getByText('Practice')).toBeInTheDocument();
    expect(within(menu).getByRole('button', { name: 'Doctor' })).toBeInTheDocument();
    expect(within(menu).getByRole('button', { name: 'Light theme' })).toBeInTheDocument();

    fireEvent.click(within(menu).getByRole('button', { name: 'AI-assisted coding' }));
    expect(await screen.findByRole('heading', { name: 'AI-assisted coding' })).toBeInTheDocument();
    expect(screen.getByText('Three-phase scenarios with an assistant.')).toBeInTheDocument();
    expect(within(menu).getByRole('button', { name: 'AI-assisted coding' })).toHaveAttribute('aria-current', 'page');
    expect(await screen.findByRole('article', { name: 'Hold Exposure' })).toBeInTheDocument();

    fireEvent.click(within(menu).getByRole('button', { name: 'Doctor' }));
    expect(await screen.findByRole('heading', { name: 'Is this machine ready?' })).toBeInTheDocument();

    fireEvent.click(within(menu).getByRole('button', { name: 'AI-assisted coding' }));
    fireEvent.click((await screen.findAllByRole('button', { name: 'Start' }))[0]);
    fireEvent.click(screen.getByRole('button', { name: 'Start session' }));
    fireEvent.click(await screen.findByRole('button', { name: 'Start phase 1' }));
    expect(await screen.findByText('room for new-1')).toBeInTheDocument();
    expect(screen.queryByRole('navigation', { name: 'Main menu' })).toBeNull();
  });

  it('collapses and expands the menu, remembering the choice', async () => {
    renderApp(fakeClient());
    const toggle = await screen.findByRole('button', { name: 'Collapse menu' });
    expect(toggle).toHaveAttribute('aria-expanded', 'true');

    fireEvent.click(toggle);

    expect(screen.getByRole('button', { name: 'Expand menu' })).toHaveAttribute('aria-expanded', 'false');
    expect(screen.getByRole('navigation', { name: 'Main menu' }).parentElement).toHaveClass('collapsed');
    expect(localStorage.getItem('intervue.menu')).toBe('collapsed');
    fireEvent.click(screen.getByRole('button', { name: 'Expand menu' }));
    expect(localStorage.getItem('intervue.menu')).toBe('open');
  });
});

describe('answer and navigation', () => {
  const answer = vi.fn(async () => ({ text: '## Minutes 0 to 6: read before you run' }));

  it('opens a problem answer from the section page before any session', async () => {
    const client = fakeClient({ walkthrough: answer });
    renderApp(client);

    fireEvent.click(await screen.findByRole('button', { name: 'AI-assisted coding' }));
    fireEvent.click((await screen.findAllByRole('button', { name: 'Answer' }))[0]);

    expect(await screen.findByRole('heading', { name: 'Minutes 0 to 6: read before you run' })).toBeInTheDocument();
    expect(client.walkthrough).toHaveBeenCalledWith('hold-exposure');
  });

  it('goes from the result to the answer and back home', async () => {
    const finished = { ...session, id: 'a', title: 'Hold Exposure', finished: true, result: null } as SessionView;
    const client = fakeClient({
      history: vi.fn(async () => [summary({ id: 'a', problemId: 'hold-exposure' })]),
      session: vi.fn(async () => finished),
      walkthrough: answer,
    });
    renderApp(client);

    fireEvent.click((await screen.findAllByRole('button', { name: 'Result' }))[0]);
    await screen.findByRole('button', { name: 'Try again' });
    fireEvent.click(screen.getByRole('button', { name: 'Answer' }));
    await screen.findByRole('heading', { name: 'Minutes 0 to 6: read before you run' });
    fireEvent.click(screen.getByRole('button', { name: 'Home' }));

    expect(await screen.findByLabelText('Recent results')).toBeInTheDocument();
  });
});

describe('start screen', () => {
  const openSetup = async () => {
    fireEvent.click(await screen.findByRole('button', { name: 'AI-assisted coding' }));
    fireEvent.click((await screen.findAllByRole('button', { name: 'Start' }))[0]);
  };

  it('sets up a timed Practice session, shows the overview, then opens the room', async () => {
    const client = fakeClient();
    renderApp(client);

    await openSetup();
    expect(screen.getByRole('heading', { name: 'Set up your session' })).toBeInTheDocument();
    expect(screen.getByRole('combobox', { name: 'Language' })).toHaveValue('java');
    expect(screen.queryByRole('checkbox', { name: /Timed/ })).toBeNull();
    fireEvent.click(screen.getByRole('radio', { name: /Practice/ }));
    fireEvent.click(screen.getByRole('checkbox', { name: /Timed/ }));
    fireEvent.click(screen.getByRole('button', { name: 'Start session' }));

    expect(await screen.findByText('3 phases, 60 minutes')).toBeInTheDocument();
    expect(screen.getByText('Peak and breach')).toBeInTheDocument();
    expect(client.runHealth).toHaveBeenCalledWith(true);
    expect(client.createSession).toHaveBeenCalledWith('hold-exposure', 'PRACTICE', true);

    fireEvent.click(screen.getByRole('button', { name: 'Start phase 1' }));
    expect(await screen.findByText('room for new-1')).toBeInTheDocument();
  });

  it('starts an Interview with the limit always on', async () => {
    const client = fakeClient();
    renderApp(client);

    await openSetup();
    fireEvent.click(screen.getByRole('radio', { name: /Interview/ }));
    fireEvent.click(screen.getByRole('button', { name: 'Start session' }));

    await screen.findByText('3 phases, 60 minutes');
    expect(client.createSession).toHaveBeenCalledWith('hold-exposure', 'INTERVIEW', true);
  });

  it('refuses to start when a blocking check fails and says which', async () => {
    const client = fakeClient({
      runHealth: vi.fn(async () => ({
        ready: false,
        results: [check({ status: 'FAIL', title: 'Sessions folder writable', message: 'Intervue cannot write to X' })],
      })),
    });
    renderApp(client);

    await openSetup();
    fireEvent.click(screen.getByRole('radio', { name: /Interview/ }));
    fireEvent.click(screen.getByRole('button', { name: 'Start session' }));

    expect(await screen.findByText(/Intervue cannot write to X/)).toBeInTheDocument();
    expect(client.createSession).not.toHaveBeenCalled();
  });
});

describe('doctor', () => {
  it('runs every check on first launch and lists the results', async () => {
    const client = fakeClient({
      health: vi.fn(async () => []),
      runHealth: vi.fn(async () => ({
        ready: true,
        results: [check({}), check({ id: 'java.calibration', title: 'Calibration (java)', measured: 'factor 1.30' })],
      })),
    });

    renderApp(client);

    expect(await screen.findByText('Calibration (java)')).toBeInTheDocument();
    expect(screen.getByText('factor 1.30')).toBeInTheDocument();
    expect(client.runHealth).toHaveBeenCalledWith(false);
    fireEvent.click(screen.getByRole('button', { name: 'Continue' }));
    await waitFor(() => expect(client.problems).toHaveBeenCalled());
  });
});
