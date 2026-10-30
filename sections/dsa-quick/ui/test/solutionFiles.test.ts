import { describe, expect, it } from 'vitest';
import { solutionFiles } from '../src/solutionFiles.ts';

const f = (path: string, readOnly = false) => ({ path, readOnly });

describe('solutionFiles', () => {
  it('skips the main class, read-only files and data/', () => {
    const files = [f('Main.java'), f('Phase1Tests.java', true), f('data/cases.txt'), f('TwoSum.java')];
    expect(solutionFiles(files)).toEqual(['TwoSum.java']);
  });

  it('lists several candidates in path order', () => {
    expect(solutionFiles([f('Util.java'), f('Main.java'), f('Solver.java')])).toEqual(['Solver.java', 'Util.java']);
  });

  it('falls back to the first editable file', () => {
    expect(solutionFiles([f('Tests.java', true), f('Main.java')])).toEqual(['Main.java']);
    expect(solutionFiles([f('Tests.java', true)])).toEqual([]);
  });
});
