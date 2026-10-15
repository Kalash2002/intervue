import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  // `vite dev` has no Pages functions; forward /api to `wrangler pages dev` when it runs on 8788.
  server: { proxy: { '/api': 'http://localhost:8788' } },
});
