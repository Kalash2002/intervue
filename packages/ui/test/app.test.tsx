import { fireEvent, render, screen, waitFor, within } from '@testing-library/react';
import type {
  CheckResult,
  EngineClient,
  ProblemSummary,
  SessionSummary,
  SessionView,
} from '@intervue/local-engine-client';
import { describe, expect, it, vi } from 'vitest';
import { App, type SectionUi } from '../src/index.ts';

const problem = (id: string, title: string): ProblemSummary => ({
  id,
  title,
  summary: `${title} summary`,
  difficulty: 'Medium',
  minutes: 60,
  phases: 3,
});

const summary = (over: Partial<SessionSummary>): SessionSummary => ({
  id: 's',
  problemId: 'hold-exposure',
  type: 'INTERVIEW',
  startedAt: 0,
  finished: true,
  total: 70,
  label: 'Interview',
  solved: true,
  countsAsSolved: true,
  ...over,
});

const check = (over: Partial<CheckResult>): CheckResult => ({
  id: 'os',
  title: 'Operating system',
  blocking: true,
  status: 'PASS',
  message: 'Windows 11',
  measured: null,
  ranAt: 0,
  ...over,
});

const session = {
  id: 'new-1',
  problemId: 'hold-exposure',
  type: 'PRACTICE',
  minutes: 60,
  phases: 3,
  phaseTitles: ['Fix the bug', 'Peak and breach', 'Busy books'],
} as SessionView;

function fakeClient(over: Partial<EngineClient> = {}): EngineClient {
  return {
    health: vi.fn(async () => [check({})]),
    runHealth: vi.fn(async () => ({ ready: true, results: [check({})] })),
    problems: vi.fn(async () => [
      problem('hold-exposure', 'Hold Exposure'),
      problem('corridor-router', 'Corridor Router'),
    ]),
    history: vi.fn(async () => []),
    createSession: vi.fn(async () => session),
    ...over,
  } as unknown as EngineClient;
}

const section: SectionUi = {
  Room: ({ session: s }) => <p>room for {s.view.id}</p>,
  Result: () => <p>result parts</p>,
};

const renderApp = (client: EngineClient) => render(<App client={client} section={section} languages={[]} />);

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

describe('dashboard', () => {
  it('counts solved interviews and labels every session Interview or Practice', async () => {
    const client = fakeClient({
      history: vi.fn(async () => [
        summary({ id: 'a', problemId: 'hold-exposure', label: 'Interview', total: 82 }),
        summary({ id: 'b', problemId: 'corridor-router', label: 'Interview', solved: false, countsAsSolved: false }),
        summary({ id: 'c', problemId: 'corridor-router', type: 'PRACTICE', label: 'Practice', countsAsSolved: false }),
      ]),
    });

    renderApp(client);

    const stats = await screen.findByLabelText('Your progress');
    expect(within(stats).getByText('Solved').previousSibling).toHaveTextContent('1 of 2');
    const history = screen.getByLabelText('Session history');
    expect(within(history).getAllByText('Interview')).toHaveLength(2);
    expect(screen.queryByText(/Interview, assisted/)).toBeNull();
    expect(screen.queryByText(/without applied AI edits/)).toBeNull();
    expect(within(history).getByText('Practice')).toBeInTheDocument();
  });

  it('shows each problem as a card with its difficulty, minutes and status', async () => {
    const client = fakeClient({ history: vi.fn(async () => [summary({ problemId: 'hold-exposure' })]) });

    renderApp(client);

    const card = await screen.findByRole('article', { name: 'Hold Exposure' });
    expect(within(card).getByText('Medium')).toHaveClass('pill');
    expect(within(card).getByText('60 min')).toBeInTheDocument();
    expect(within(card).getByText(/Solved/)).toBeInTheDocument();
    expect(
      within(screen.getByRole('article', { name: 'Corridor Router' })).getByText(/Not started/),
    ).toBeInTheDocument();
  });
});

describe('start screen', () => {
  it('sets up a timed Practice session, shows the overview, then opens the room', async () => {
    const client = fakeClient();
    renderApp(client);

    fireEvent.click((await screen.findAllByRole('button', { name: 'Start' }))[0]);
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

    fireEvent.click((await screen.findAllByRole('button', { name: 'Start' }))[0]);
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

    fireEvent.click((await screen.findAllByRole('button', { name: 'Start' }))[0]);
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
