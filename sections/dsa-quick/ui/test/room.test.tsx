import { fireEvent, screen, waitFor } from '@testing-library/react';
import type { EngineClient } from '@intervue/local-engine-client';
import { timed } from '@intervue/ui';
import { describe, expect, it, vi } from 'vitest';
import { SolveRoom } from '../src/SolveRoom.tsx';
import { renderWith, view } from './fixtures.tsx';

function client(over: Partial<EngineClient> = {}): Partial<EngineClient> {
  return {
    aiStatus: vi.fn(async () => ({ developer: false, available: true, reason: '' })),
    chat: vi.fn(async () => []),
    session: vi.fn(async () => view()),
    files: vi.fn(async () => [
      { path: 'Main.java', readOnly: false },
      { path: 'Phase1Tests.java', readOnly: true },
      { path: 'TwoSum.java', readOnly: false },
    ]),
    readFile: vi.fn(async (_id: string, path: string) => ({ text: `// contents of ${path}` })),
    writeFile: vi.fn(async () => ({ ok: true })),
    symbols: vi.fn(async () => []),
    diagnostics: vi.fn(async () => []),
    run: vi.fn(async () => ({ output: 'PASS 3/3', diagnostics: [], newlyPassed: [] })),
    finish: vi.fn(),
    ...over,
  } as Partial<EngineClient>;
}

describe('the DSA room', () => {
  it('offers small tabs when several files qualify', async () => {
    const files = [
      { path: 'Main.java', readOnly: false },
      { path: 'Solver.java', readOnly: false },
      { path: 'Util.java', readOnly: false },
    ];
    const { container } = renderWith(
      <SolveRoom session={timed(view())} />,
      client({ files: vi.fn(async () => files) }),
    );
    await waitFor(() => expect(container.querySelector('.cm-content')?.textContent).toContain('Solver.java'));

    fireEvent.click(screen.getByRole('tab', { name: 'Util.java' }));
    await waitFor(() => expect(container.querySelector('.cm-content')?.textContent).toContain('Util.java'));
  });

  it('shows the statement and no empty intro, in the solution file', async () => {
    const { container } = renderWith(<SolveRoom session={timed(view())} />, client());

    expect(screen.getByText(/Find two indices/)).toBeInTheDocument();
    expect(container.querySelectorAll('.statement .md').length).toBe(1);
    await waitFor(() => expect(container.querySelector('.cm-content')).not.toBeNull());
    expect(container.querySelector('.cm-content')?.textContent).toContain('contents of TwoSum.java');
    expect(screen.queryByRole('tab', { name: /java/ })).toBeNull(); // one candidate: no tabs
  });

  it('runs the tests through the client', async () => {
    const c = client();
    renderWith(<SolveRoom session={timed(view())} />, c);
    const run = await screen.findByRole('button', { name: 'Run tests' });
    await waitFor(() => expect(run).toBeEnabled());
    fireEvent.click(run);

    expect(screen.queryByText('Main (demo)')).toBeNull();
    expect(screen.queryByRole('combobox')).toBeNull();
    await waitFor(() => expect(c.run).toHaveBeenCalledWith('s1', 'phase:1'));
  });

  it('asks before finishing', async () => {
    renderWith(<SolveRoom session={timed(view())} />, client());
    fireEvent.click(screen.getByRole('button', { name: 'Finish' }));

    expect(screen.getByRole('dialog', { name: 'End the session?' })).toBeInTheDocument();
  });

  it.each(['INTERVIEW', 'PRACTICE'] as const)('%s: Hints, no Assistant', (type) => {
    renderWith(<SolveRoom session={timed(view({ type }))} />, client());

    expect(screen.getByRole('tab', { name: 'Hints' })).toHaveAttribute('aria-selected', 'true');
    expect(screen.queryByRole('tab', { name: 'Assistant' })).toBeNull();
  });

  it('shows no Assistant tab when channels are empty, whatever the section', () => {
    renderWith(<SolveRoom session={timed(view({ section: 'other-section' }))} />, client());

    expect(screen.queryByRole('tab', { name: 'Assistant' })).toBeNull();
  });

  it('shows the Assistant tab, first, when channels hold ASK', () => {
    renderWith(<SolveRoom session={timed(view({ channels: ['ASK'] }))} />, client());

    expect(screen.getByRole('tab', { name: 'Assistant' })).toHaveAttribute('aria-selected', 'true');
    expect(screen.getByRole('tab', { name: 'Hints' })).toBeInTheDocument();
  });

  it('shows no side panel when neither hints nor assistant apply', () => {
    renderWith(<SolveRoom session={timed(view({ hintsAllowed: false }))} />, client());

    expect(screen.queryByRole('tab', { name: 'Hints' })).toBeNull();
    expect(screen.queryByRole('tab', { name: 'Assistant' })).toBeNull();
    expect(screen.queryByRole('complementary')).toBeNull();
  });
});
