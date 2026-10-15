// Walkthrough links to in-app DSA questions render as chips; ordinary links and the sanitiser
// are unchanged.
import { fireEvent, render, screen } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { AppContext, Markdown, type AppServices } from '../src/index.ts';

const services = { notify: vi.fn(), go: vi.fn(), sections: [], languages: [] } as unknown as AppServices;
const renderMd = (source: string, onDsa?: (id: string) => void) =>
  render(
    <AppContext.Provider value={services}>
      <Markdown source={source} onDsa={onDsa} />
    </AppContext.Provider>,
  );

describe('Markdown', () => {
  it('renders a dsa:// link as a chip that reports its question id when clicked', () => {
    const onDsa = vi.fn();
    renderMd('Practice [Two Sum](dsa://two-sum) and [Top K](dsa://top-k-frequent-elements).', onDsa);

    const chip = screen.getByRole('link', { name: 'Two Sum' });
    expect(chip.tagName).toBe('SPAN');
    expect(chip).toHaveClass('dsa-chip');
    expect(chip).toHaveAttribute('data-id', 'two-sum');
    fireEvent.click(chip);
    expect(onDsa).toHaveBeenCalledWith('two-sum');
    expect(screen.getByRole('link', { name: 'Top K' })).toHaveAttribute('data-id', 'top-k-frequent-elements');
  });

  it('says the DSA section is coming when no handler is given', () => {
    renderMd('[Two Sum](dsa://two-sum)');

    fireEvent.click(screen.getByRole('link', { name: 'Two Sum' }));

    expect(services.notify).toHaveBeenCalledWith('DSA arrives in the next release.');
  });

  it('leaves ordinary links as anchors and still strips script URLs', () => {
    const { container } = renderMd('[Docs](https://example.com) and [bad](javascript:alert(1))');

    expect(screen.getByRole('link', { name: 'Docs' }).tagName).toBe('A');
    expect(screen.getByRole('link', { name: 'Docs' })).toHaveAttribute('href', 'https://example.com');
    expect(container.querySelector('a[href^="javascript"]')).toBeNull();
    expect(container.textContent).toContain('bad');
  });
});
