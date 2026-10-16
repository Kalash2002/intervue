import { screen, within } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import { Statement } from '../src/Statement.tsx';
import { renderWith } from './fixtures.tsx';

const SHAPED = `# Pair Sum

Return the indices of two values that add up to the target.

## Example 1

**Input:** \`nums = [2,7], target = 9\`
**Output:** \`[0,1]\`
**Explanation:** 2 + 7 is 9.

## Example 2

**Input:** \`nums = [3,3], target = 6\`
**Output:** \`[0,1]\`
**Explanation:** Equal values still use two positions.

## Constraints

- 2 <= nums.length <= 1000
- exactly one answer exists

## Follow-up

Can you do it in one pass?
`;

describe('Statement', () => {
  it('boxes each example with its three labels and lists the constraints', () => {
    const { container } = renderWith(<Statement source={SHAPED} />, {});

    expect(screen.getByRole('heading', { name: 'Pair Sum' })).toBeInTheDocument();
    const boxes = container.querySelectorAll('section.example');
    expect(boxes.length).toBe(2);
    expect(screen.getByLabelText('Example 2')).toBe(boxes[1]);
    for (const label of ['Input', 'Output', 'Explanation']) {
      expect(within(boxes[0] as HTMLElement).getByText(label)).toBeInTheDocument();
    }
    expect(container.querySelector('.example code')?.textContent).toBe('nums = [2,7], target = 9');
    const items = within(screen.getByLabelText('Constraints')).getAllByRole('listitem');
    expect(items.map((i) => i.textContent)).toEqual(['2 <= nums.length <= 1000', 'exactly one answer exists']);
    expect(screen.getByText('Can you do it in one pass?')).toBeInTheDocument();
  });

  it('falls back to plain Markdown without an Example 1 heading', () => {
    const { container } = renderWith(<Statement source={'Find two indices.\n\n## Notes\n\nPlain.'} />, {});

    expect(container.querySelector('section.example')).toBeNull();
    expect(container.querySelectorAll('.md').length).toBe(1);
    expect(screen.getByRole('heading', { name: 'Notes' })).toBeInTheDocument();
  });
});
