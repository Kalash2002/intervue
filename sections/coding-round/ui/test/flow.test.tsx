import { fireEvent, screen, waitFor, within } from '@testing-library/react';
import type { EngineClient } from '@intervue/local-engine-client';
import { timed } from '@intervue/ui';
import { describe, expect, it, vi } from 'vitest';
import { SessionRoom } from '../src/room/SessionRoom.tsx';
import { event, renderWith, view } from './fixtures.tsx';

function client(over: Partial<EngineClient> = {}): Partial<EngineClient> {
  return {
    session: vi.fn(async () => view()),
    files: vi.fn(async () => [{ path: 'Calc.java', readOnly: false }]),
    readFile: vi.fn(async () => ({ text: 'class Calc {}' })),
    symbols: vi.fn(async () => []),
    diagnostics: vi.fn(async () => []),
    aiStatus: vi.fn(async () => ({ developer: false, available: false, reason: 'off' })),
    chat: vi.fn(async () => []),
    skip: vi.fn(async () => view({ unlocked: 2 })),
    run: vi.fn(async () => ({
      target: 'phase:1',
      compileError: false,
      timedOut: false,
      output: 'PASS  twiceDoubles\nFAIL  twiceOfZero',
      diagnostics: [],
      phases: [],
      newlyPassed: [],
      unlocked: 1,
      aiEditsActive: 0,
    })),
    finish: vi.fn(async () => view({ finished: true })),
    ...over,
  };
}

describe('the phase intro card', () => {
  it('opens each phase with its name, one sentence, Start and Skip phase', async () => {
    renderWith(<SessionRoom session={timed(view())} />, client());

    const card = await screen.findByRole('region', { name: 'Phase 1: Fix' });
    expect(within(card).getByText('Twice adds one too many.')).toBeInTheDocument();
    fireEvent.click(within(card).getByRole('button', { name: 'Start' }));

    expect(screen.queryByRole('region', { name: 'Phase 1: Fix' })).toBeNull();
    expect(screen.getByText('Phase 1 tests must pass')).toBeInTheDocument();
  });

  it('skips the phase through the engine', async () => {
    const c = client();
    renderWith(<SessionRoom session={timed(view())} />, c);

    fireEvent.click(await screen.findByRole('button', { name: 'Skip phase' }));

    await waitFor(() => expect(c.skip).toHaveBeenCalledWith('s1'));
  });

  it('is not shown again once the phase has a run', async () => {
    const v = view({
      events: [event(1, 'run', 1000, { target: 'phase:1', passed: 2, failed: 1, compileError: false })],
    });
    renderWith(<SessionRoom session={timed(v)} />, client());

    await screen.findByText('Phase 1 tests must pass');
    expect(screen.queryByRole('region', { name: 'Phase 1: Fix' })).toBeNull();
  });
});

describe('the Guide tab', () => {
  it('shows how many of the phase tests passed last time', async () => {
    const v = view({
      events: [event(1, 'run', 1000, { target: 'phase:1', passed: 2, failed: 1, compileError: false })],
    });
    renderWith(<SessionRoom session={timed(v)} />, client());

    expect(await screen.findByText('2 of 3 passing')).toBeInTheDocument();
  });

  it('runs the phase tests and shows the output in the tab too', async () => {
    const v = view({
      events: [event(1, 'run', 1000, { target: 'phase:1', passed: 0, failed: 1, compileError: false })],
    });
    // The engine's view after the run still carries the earlier run event.
    const c = client({ session: vi.fn(async () => v) });
    renderWith(<SessionRoom session={timed(v)} />, c);

    fireEvent.click(await screen.findByRole('button', { name: 'Run tests' }));

    await waitFor(() => expect(c.run).toHaveBeenCalledWith('s1', 'phase:1'));
    const guide = screen.getByRole('tabpanel', { name: 'Guide' });
    expect(await within(guide).findByText(/twiceOfZero/)).toBeInTheDocument();
  });
});

describe('the header', () => {
  it('shows one progress dot per phase', async () => {
    renderWith(<SessionRoom session={timed(view({ phasesPassed: { '1': 5000 }, unlocked: 2 }))} />, client());

    const dots = await screen.findByRole('list', { name: 'Phase progress' });
    expect(within(dots).getByLabelText('Phase 1: passed')).toBeInTheDocument();
    expect(within(dots).getByLabelText('Phase 2: open')).toBeInTheDocument();
  });
});

describe('ending the session', () => {
  it('asks how to end: Cancel, End early or I am done', async () => {
    const c = client();
    renderWith(<SessionRoom session={timed(view())} />, c);

    fireEvent.click(await screen.findByRole('button', { name: 'Finish' }));
    const dialog = screen.getByRole('dialog', { name: 'End the session?' });
    fireEvent.click(within(dialog).getByRole('button', { name: 'Cancel' }));
    expect(screen.queryByRole('dialog')).toBeNull();

    fireEvent.click(screen.getByRole('button', { name: 'Finish' }));
    fireEvent.click(within(screen.getByRole('dialog')).getByRole('button', { name: 'End early' }));
    await waitFor(() => expect(c.finish).toHaveBeenCalledWith('s1', true));
  });

  it('finishes normally with I am done', async () => {
    const c = client();
    renderWith(<SessionRoom session={timed(view())} />, c);

    fireEvent.click(await screen.findByRole('button', { name: 'Finish' }));
    fireEvent.click(within(screen.getByRole('dialog')).getByRole('button', { name: "I'm done" }));

    await waitFor(() => expect(c.finish).toHaveBeenCalledWith('s1', false));
  });
});
