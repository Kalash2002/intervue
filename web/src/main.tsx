import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import CssBaseline from '@mui/material/CssBaseline';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import posthog from 'posthog-js';
import App from './App';

// Without a key (local dev, forks) analytics stay off and posthog.capture is a no-op.
const posthogKey = import.meta.env.VITE_POSTHOG_KEY;
if (posthogKey) {
  posthog.init(posthogKey, {
    api_host: import.meta.env.VITE_POSTHOG_HOST || 'https://us.i.posthog.com',
    // Only pageviews and the named events below; autocapture would also record form interactions.
    autocapture: false,
    person_profiles: 'identified_only',
  });
}

const theme = createTheme({ palette: { mode: 'dark', primary: { main: '#7cc4ff' } } });

const root = document.getElementById('root');
if (!root) throw new Error('index.html is missing #root');

createRoot(root).render(
  <StrictMode>
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <App />
    </ThemeProvider>
  </StrictMode>,
);
