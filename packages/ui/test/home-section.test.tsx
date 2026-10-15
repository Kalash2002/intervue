// Home (continue, tracks, recent results, streak) and the section page (filters, grouped and flat
// lists, next up, random unsolved), as docs/design/home-and-practice.md sections 4 and 5 say.
import { fireEvent, screen, within } from '@testing-library/react';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { forgetFilters } from '../src/pages/SectionPage.tsx';
import { fakeClient, problem, renderApp, summary } from './fixtures.tsx';

const today = new Date(2026, 9, 15, 15).getTime();
const yesterday = today - 24 * 60 * 60 * 1000;

const problems = [
  problem('hold-exposure', 'Hold Exposure', {
    difficulty: 'Easy',
    techniques: ['intervals'],
    tags: ['fintech', 'intervals'],
    free: true,
  }),
  problem('corridor-router', 'Corridor Router', { techniques: ['graphs'], tags: ['fintech', 'graphs'] }),
  problem('merchant-suggest', 'Merchant Suggest', {
    difficulty: 'Hard',
    techniques: ['tries'],
    tags: ['fintech', 'tries'],
  }),
];
const history = [
  summary({ id: 'a', problemId: 'hold-exposure', total: 82, startedAt: today - 1000 }),
  summary({
    id: 'b',
    problemId: 'corridor-router',
    finished: false,
    total: null,
    solved: false,
    countsAsSolved: false,
    startedAt: yesterday,
  }),
];

function client() {
  return fakeClient({ problems: vi.fn(async () => problems), history: vi.fn(async () => history) });
}

afterEach(() => {
  vi.useRealTimers();
  forgetFilters();
});

describe('home', () => {
  it('greets, shows the open session to continue, the tracks and the recent results', async () => {
    vi.useFakeTimers({ now: today, shouldAdvanceTime: true });
    renderApp(client());

    expect(await screen.findByRole('heading', { name: /Good (morning|afternoon|evening)/ })).toBeInTheDocument();
    const next = await screen.findByLabelText('Continue');
    expect(within(next).getByText('Corridor Router')).toBeInTheDocument();
    expect(within(next).getByRole('button', { name: 'Resume' })).toBeInTheDocument();

    const tracks = screen.getByLabelText('Tracks');
    const coding = within(tracks).getByRole('article', { name: 'AI-assisted coding' });
    expect(within(coding).getByText('1 of 3 solved')).toBeInTheDocument();
    expect(within(coding).getAllByRole('listitem')).toHaveLength(18);
    expect(within(coding).getByTitle('Intervals: 1 of 1 solved')).toHaveClass('done');
    expect(within(coding).getByTitle('Graphs: 0 of 1 solved')).not.toHaveClass('done');
    expect(within(tracks).getByRole('article', { name: 'DSA' })).toHaveTextContent('Arrives in the next release');

    const recent = screen.getByLabelText('Recent results');
    expect(within(recent).getByText('Hold Exposure')).toBeInTheDocument();
    expect(within(recent).getByText('82 / 100')).toBeInTheDocument();
    expect(within(recent).queryByText('Corridor Router')).toBeNull();
    expect(within(recent).getByRole('button', { name: 'Answer' })).toBeInTheDocument();

    expect(screen.getByLabelText('Streak')).toHaveTextContent('1 day');
    expect(within(screen.getByLabelText('Last seven days')).getAllByRole('listitem')).toHaveLength(7);
  });

  it('offers the next problem per section when nothing is open', async () => {
    renderApp(fakeClient({ problems: vi.fn(async () => problems), history: vi.fn(async () => history.slice(0, 1)) }));

    const next = await screen.findByLabelText('Continue');
    expect(within(next).getByRole('heading', { level: 3 })).toHaveTextContent('Next: Merchant Suggest');
    fireEvent.click(within(next).getByRole('button', { name: 'Start' }));
    expect(await screen.findByRole('heading', { name: 'Set up your session' })).toBeInTheDocument();
  });

  it('opens a section from a track card', async () => {
    renderApp(client());
    fireEvent.click(await screen.findByRole('button', { name: 'Open AI-assisted coding' }));
    expect(await screen.findByRole('heading', { name: 'AI-assisted coding' })).toBeInTheDocument();
  });
});

describe('section page', () => {
  const open = async () => {
    renderApp(client());
    fireEvent.click(await screen.findByRole('button', { name: 'AI-assisted coding' }));
    return screen.findByRole('heading', { name: 'AI-assisted coding' });
  };
  const cards = () =>
    screen.queryAllByRole('article').filter((a) => a.classList.contains('card') && !a.classList.contains('track'));
  const titles = () => cards().map((c) => within(c).getByRole('heading').textContent);

  it('shows the stat tiles, next up, the free pill and the tags', async () => {
    await open();

    expect(within(screen.getByLabelText('Your progress')).getByText('Solved').previousSibling).toHaveTextContent(
      '1 of 3',
    );
    expect(screen.getByLabelText('Next up')).toHaveTextContent('Merchant Suggest');
    const free = screen.getByRole('article', { name: 'Hold Exposure' });
    expect(within(free).getByText('Free')).toBeInTheDocument();
    expect(within(free).getByRole('button', { name: 'intervals' })).toHaveClass('chip');
  });

  it('groups by topic in roadmap order and hides empty groups; flat view sorts by difficulty', async () => {
    await open();

    const groups = () => screen.queryAllByText(/.+/, { selector: '.group-title' }).map((g) => g.textContent);
    expect(groups()).toEqual(['Tries', 'Graphs', 'Intervals']);
    expect(screen.getByText('Graphs', { selector: '.group-title' }).parentElement).toHaveTextContent('0/1');

    fireEvent.click(screen.getByRole('button', { name: 'Flat' }));
    expect(groups()).toHaveLength(0);
    expect(titles()).toEqual(['Hold Exposure', 'Corridor Router', 'Merchant Suggest']);
  });

  it('filters by search, difficulty, status and tag, ANDed across kinds, and resets', async () => {
    await open();

    fireEvent.change(screen.getByRole('searchbox', { name: 'Search problems' }), { target: { value: 'corr' } });
    expect(titles()).toEqual(['Corridor Router']);
    fireEvent.change(screen.getByRole('searchbox', { name: 'Search problems' }), { target: { value: '' } });

    fireEvent.click(screen.getByRole('button', { name: 'Easy' }));
    expect(titles()).toEqual(['Hold Exposure']);
    fireEvent.click(screen.getByRole('button', { name: 'Solved' }));
    expect(titles()).toEqual(['Hold Exposure']);
    fireEvent.click(screen.getByRole('button', { name: 'Hard' }));
    expect(titles()).toEqual(['Hold Exposure']);
    fireEvent.click(screen.getByRole('button', { name: 'Not started' }));
    expect(titles()).toEqual(['Merchant Suggest', 'Hold Exposure']);
    fireEvent.click(screen.getByRole('button', { name: 'Reset' }));
    expect(titles()).toHaveLength(3);

    const tagBar = screen.getByRole('group', { name: 'Filter by tag' });
    expect(within(tagBar).getByRole('button', { name: /fintech/ })).toHaveTextContent('3');
    fireEvent.click(within(tagBar).getByRole('button', { name: /graphs/ }));
    expect(titles()).toEqual(['Corridor Router']);
    expect(screen.getByText('1 of 3 problems')).toBeInTheDocument();
  });

  it('remembers the filter while the app runs and takes a tag from a chip', async () => {
    await open();
    fireEvent.change(screen.getByRole('searchbox', { name: 'Search problems' }), { target: { value: 'hold' } });
    fireEvent.click(screen.getByRole('button', { name: 'Home' }));
    await screen.findByLabelText('Continue');

    fireEvent.click(screen.getByRole('button', { name: 'AI-assisted coding' }));
    expect(await screen.findByRole('searchbox', { name: 'Search problems' })).toHaveValue('hold');
    expect(titles()).toEqual(['Hold Exposure']);

    fireEvent.click(
      within(screen.getByRole('article', { name: 'Hold Exposure' })).getByRole('button', { name: 'intervals' }),
    );
    expect(await screen.findByRole('searchbox', { name: 'Search problems' })).toHaveValue('');
    expect(
      within(screen.getByRole('group', { name: 'Filter by tag' })).getByRole('button', { name: /intervals/ }),
    ).toHaveClass('on');
    expect(titles()).toEqual(['Hold Exposure']);
  });

  it('starts a random unsolved problem', async () => {
    await open();
    vi.spyOn(Math, 'random').mockReturnValue(0.99);

    fireEvent.click(screen.getByRole('button', { name: 'Random unsolved' }));

    expect(await screen.findByRole('heading', { name: 'Set up your session' })).toBeInTheDocument();
    expect(screen.getByText('Merchant Suggest')).toBeInTheDocument();
  });
});
