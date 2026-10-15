import js from '@eslint/js';
import tseslint from 'typescript-eslint';
import reactHooks from 'eslint-plugin-react-hooks';

export default tseslint.config(
  { ignores: ['dist', '.wrangler'] },
  js.configs.recommended,
  tseslint.configs.strict,
  reactHooks.configs.flat.recommended,
  {
    rules: {
      // Limits from docs/process/definition-of-done.md.
      complexity: ['error', 10],
      'max-lines': ['error', 400],
      'max-lines-per-function': ['error', 60],
    },
  },
);
