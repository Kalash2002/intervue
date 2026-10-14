import { readFileSync } from 'node:fs';
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Browser dev mode (`pnpm dev:browser` plus `pnpm engine:dev`): the engine writes its token to
// .dev-data/dev-token and the proxy adds it, so the token never reaches the page. Inside the Tauri
// shell the page talks to the engine directly and this proxy is unused.
const devToken = (): string | null => {
  try {
    return readFileSync(new URL('.dev-data/dev-token', import.meta.url), 'utf8').trim();
  } catch {
    return null;
  }
};

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
        // A throw inside a proxy event handler kills the dev server (and `tauri dev` with it), so a
        // missing token only means the request goes without one and the engine answers 401.
        configure: (proxy) =>
          proxy.on('proxyReq', (req) => {
            const token = devToken();
            if (token) {
              req.setHeader('Authorization', `Bearer ${token}`);
            } else {
              console.warn('[intervue] no browser-mode engine: run `pnpm -C desktop engine:dev`');
            }
          }),
      },
    },
  },
  build: { outDir: 'dist', target: 'es2022' },
});
