import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Container from '@mui/material/Container';
import Grid from '@mui/material/Grid';
import Link from '@mui/material/Link';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import posthog from 'posthog-js';
import WaitlistForm from './WaitlistForm';

// Images are dropped into public/screens/ by hand; see web/README.md.
const SCREENS = [
  { src: '/screens/screen-1.png', caption: 'A timed session in a codebase you did not write.' },
  { src: '/screens/screen-2.png', caption: 'An assistant that answers questions or writes code, like the real round.' },
  { src: '/screens/screen-3.png', caption: 'A score from the tests and a written review of how you worked.' },
];

const prototypeUrl = import.meta.env.VITE_PROTOTYPE_URL;

function Screens() {
  return (
    <Grid container spacing={3} sx={{ mb: 10 }}>
      {SCREENS.map((screen) => (
        <Grid key={screen.src} size={{ xs: 12, md: 4 }}>
          <Link href={screen.src} target="_blank" rel="noopener">
            <Box
              component="img"
              src={screen.src}
              alt={screen.caption}
              loading="lazy"
              sx={{ width: '100%', borderRadius: 2, border: 1, borderColor: 'divider', display: 'block' }}
            />
          </Link>
          <Typography variant="body2" color="text.secondary" sx={{ mt: 1.5 }}>
            {screen.caption}
          </Typography>
        </Grid>
      ))}
    </Grid>
  );
}

export default function App() {
  return (
    <Container maxWidth="lg" component="main" sx={{ py: { xs: 6, md: 10 } }}>
      <Stack spacing={3} sx={{ maxWidth: 760, mb: 8 }}>
        <Typography variant="overline" color="primary">
          Dryrun
        </Typography>
        <Typography variant="h2" component="h1" sx={{ fontWeight: 700, fontSize: { xs: '2.2rem', md: '3.4rem' } }}>
          Rehearse the real AI-assisted interview on your own machine.
        </Typography>
        <Typography variant="h6" color="text.secondary" sx={{ fontWeight: 400 }}>
          Same constraints as the real round: a timer, an unfamiliar codebase and an AI assistant. You get a score and a
          written review you can act on. Built for engineers with zero to five years of experience and final-year
          students.
        </Typography>
        <Box>
          <Button variant="contained" size="large" href="#waitlist">
            Join the waitlist
          </Button>
        </Box>
      </Stack>

      <Screens />

      <Box id="waitlist" sx={{ maxWidth: 560, scrollMarginTop: 32 }}>
        <Typography variant="h4" component="h2" gutterBottom>
          Join the waitlist
        </Typography>
        <Typography color="text.secondary" sx={{ mb: 3 }}>
          Four quick questions and your email. We use your email only to tell you when Dryrun opens.
        </Typography>
        <WaitlistForm />
        {prototypeUrl && (
          <Typography variant="body2" color="text.secondary" sx={{ mt: 4 }}>
            Have a JDK installed?{' '}
            <Link href={prototypeUrl} onClick={() => posthog.capture('prototype_download_clicked')}>
              Download the early prototype
            </Link>{' '}
            and try a session today.
          </Typography>
        )}
      </Box>
    </Container>
  );
}
