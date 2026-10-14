// Lint rules every TypeScript package shares (docs/process/definition-of-done.md, Lint and format).
// Packages spread this array into their own eslint.config.js and add only what is specific to them.
import js from '@eslint/js';
import tseslint from 'typescript-eslint';
import reactHooks from 'eslint-plugin-react-hooks';

const layerRule = (forbidden) => ['error', { patterns: forbidden.map((dir) => ({ group: [`**/${dir}/**`], message: `Layers point downward only (PLAN.md 5.3): do not import from ${dir}/.` })) }];

export default tseslint.config(
  { ignores: ['**/dist', '**/coverage', '**/.wrangler', '**/src-tauri/target'] },
  js.configs.recommended,
  tseslint.configs.strict,
  reactHooks.configs.flat.recommended,
  {
    rules: {
      complexity: ['error', 10],
      'max-lines': ['error', 400],
      'max-lines-per-function': ['error', 60],
    },
  },
  // A describe block is a list of cases, not one function to split.
  { files: ['**/test/**', '**/*.test.{ts,tsx}'], rules: { 'max-lines-per-function': 'off' } },
  // pages -> features -> components -> api
  { files: ['**/src/features/**'], rules: { 'no-restricted-imports': layerRule(['pages']) } },
  { files: ['**/src/components/**'], rules: { 'no-restricted-imports': layerRule(['pages', 'features']) } },
  { files: ['**/src/api/**'], rules: { 'no-restricted-imports': layerRule(['pages', 'features', 'components']) } },
);
