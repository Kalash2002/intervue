// Walkthrough links to in-app DSA questions render as chips; ordinary links and the sanitiser
// are unchanged.
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { AppContext, Markdown, type AppServices } from '../src/index.ts';

const twoSum = { id: 'two-sum', title: 'Two Sum' };
const services = {
  notify: vi.fn(),
  go: vi.fn(),
  sections: [],
  languages: [],
  client: { problems: vi.fn(async () => [twoSum]) },
} as unknown as AppServices;
const renderMd = (source: string, figures?: Record<string, string>) =>
  render(
    <AppContext.Provider value={services}>
      <Markdown source={source} figures={figures} />
    </AppContext.Provider>,
  );

describe('Markdown', () => {
  it('renders a dsa:// link as a chip carrying its question id', () => {
    renderMd('Practice [Two Sum](dsa://two-sum) and [Top K](dsa://top-k-frequent-elements).');

    const chip = screen.getByRole('link', { name: 'Two Sum' });
    expect(chip.tagName).toBe('SPAN');
    expect(chip).toHaveClass('dsa-chip');
    expect(chip).toHaveAttribute('data-id', 'two-sum');
    expect(screen.getByRole('link', { name: 'Top K' })).toHaveAttribute('data-id', 'top-k-frequent-elements');
  });

  it('opens the start screen of a DSA question on click and on Enter', async () => {
    renderMd('[Two Sum](dsa://two-sum)');
    const chip = screen.getByRole('link', { name: 'Two Sum' });

    fireEvent.click(chip);
    await waitFor(() => expect(services.go).toHaveBeenCalledWith({ name: 'start', problem: twoSum }));
    vi.mocked(services.go).mockClear();
    fireEvent.keyDown(chip, { key: 'Enter' });
    await waitFor(() => expect(services.go).toHaveBeenCalledWith({ name: 'start', problem: twoSum }));
  });

  it('says the question arrives later when its id is not in the problem list', async () => {
    renderMd('[Gone](dsa://not-yet-written)');

    fireEvent.click(screen.getByRole('link', { name: 'Gone' }));

    await waitFor(() => expect(services.notify).toHaveBeenCalledWith('This question arrives in a later batch.'));
  });

  it('renders an image as a figure from the inlined data, or its caption alone when missing', () => {
    const svg = 'data:image/svg+xml;base64,' + btoa('<svg xmlns="http://www.w3.org/2000/svg"/>');
    const { container } = renderMd('![A stack of columns](figures/stack.svg)' + '\n\n' + '![Gone](figures/gone.svg)', {
      'figures/stack.svg': svg,
    });

    const img = screen.getByRole('img', { name: 'A stack of columns' });
    expect(img).toHaveAttribute('src', svg);
    expect(img.closest('figure')).toHaveTextContent('A stack of columns');
    expect(container.querySelector('figure.missing')).toHaveTextContent('Gone');
    expect(screen.queryByRole('img', { name: 'Gone' })).toBeNull();
  });

  it('leaves ordinary links as anchors and still strips script URLs', () => {
    const { container } = renderMd('[Docs](https://example.com) and [bad](javascript:alert(1))');

    expect(screen.getByRole('link', { name: 'Docs' }).tagName).toBe('A');
    expect(screen.getByRole('link', { name: 'Docs' })).toHaveAttribute('href', 'https://example.com');
    expect(container.querySelector('a[href^="javascript"]')).toBeNull();
    expect(container.textContent).toContain('bad');
  });
});
