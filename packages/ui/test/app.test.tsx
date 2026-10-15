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

const session = { id: 'new-1', problemId: 'hold-exposure', type: 'PRACTICE' } as SessionView;

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

describe('dashboard', () => {
  it('counts only unassisted interviews as solved and labels every session', async () => {
    const client = fakeClient({
      history: vi.fn(async () => [
        summary({ id: 'a', problemId: 'hold-exposure', label: 'Interview', total: 82 }),
        summary({ id: 'b', problemId: 'corridor-router', label: 'Interview, assisted', countsAsSolved: false }),
        summary({ id: 'c', problemId: 'corridor-router', type: 'PRACTICE', label: 'Practice', countsAsSolved: false }),
      ]),
    });

    renderApp(client);

    const stats = await screen.findByLabelText('Your progress');
    expect(within(stats).getByText('Solved').previousSibling).toHaveTextContent('1 of 2');
    const history = screen.getByLabelText('Session history');
    expect(within(history).getByText('Interview')).toBeInTheDocument();
    expect(within(history).getByText('Interview, assisted')).toBeInTheDocument();
    expect(within(history).getByText('Practice')).toBeInTheDocument();
  });
});

describe('answer and navigation', () => {
  const answer = vi.fn(async () => ({ text: '## Minutes 0 to 6: read before you run' }));

  it('opens a problem answer from the dashboard before any session', async () => {
    const client = fakeClient({ walkthrough: answer });
    renderApp(client);

    fireEvent.click((await screen.findAllByRole('button', { name: 'Answer' }))[0]);

    expect(await screen.findByRole('heading', { name: 'Minutes 0 to 6: read before you run' })).toBeInTheDocument();
    expect(client.walkthrough).toHaveBeenCalledWith('hold-exposure');
  });

  it('goes from the result to the answer and back to the dashboard', async () => {
    const finished = { ...session, id: 'a', title: 'Hold Exposure', finished: true, result: null } as SessionView;
    const client = fakeClient({
      history: vi.fn(async () => [summary({ id: 'a', problemId: 'hold-exposure' })]),
      session: vi.fn(async () => finished),
      walkthrough: answer,
    });
    renderApp(client);

    fireEvent.click((await screen.findAllByRole('button', { name: 'Last result' }))[0]);
    await screen.findByRole('button', { name: 'Try again' });
    fireEvent.click(screen.getByRole('button', { name: 'Answer' }));
    await screen.findByRole('heading', { name: 'Minutes 0 to 6: read before you run' });
    fireEvent.click(screen.getByRole('button', { name: '← Dashboard' }));

    expect(await screen.findAllByRole('button', { name: 'Answer' })).toHaveLength(2);
  });
});

describe('start screen', () => {
  it('starts a Practice session after the fast checks pass', async () => {
    const client = fakeClient();
    renderApp(client);

    fireEvent.click((await screen.findAllByRole('button', { name: 'Start' }))[0]);
    fireEvent.click(screen.getByRole('button', { name: /Practice/ }));

    expect(await screen.findByText('room for new-1')).toBeInTheDocument();
    expect(client.runHealth).toHaveBeenCalledWith(true);
    expect(client.createSession).toHaveBeenCalledWith('hold-exposure', 'PRACTICE');
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
    fireEvent.click(screen.getByRole('button', { name: /Interview/ }));

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
