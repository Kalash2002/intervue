import { defineConfig } from 'vitest/config';

// The room stylesheet is processed (not blanked) so the layout test can read its text.
export default defineConfig({
  test: { environment: 'jsdom', setupFiles: ['./test/setup.ts'], css: { include: [/room\.css/] } },
});
