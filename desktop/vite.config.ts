import { readFileSync } from 'node:fs';
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Browser dev mode (`pnpm dev:browser` plus `pnpm engine:dev`): the engine writes its token to
// .dev-data/dev-token and the proxy adds it, so the token never reaches the page.
const devToken = () => readFileSync(new URL('.dev-data/dev-token', import.meta.url), 'utf8').trim();

export default defineConfig({
  plugins: [react()],
  clearScreen: false,
  server: {
    port: 1420,
    strictPort: true,
    proxy: {
      '/engine': {
        target: 'http://127.0.0.1:4100',
        rewrite: (path) => path.replace(/^\/engine/, ''),
        configure: (proxy) => proxy.on('proxyReq', (req) => req.setHeader('Authorization', `Bearer ${devToken()}`)),
      },
    },
  },
  build: { outDir: 'dist', target: 'es2022' },
});
