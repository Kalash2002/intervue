/**
 * The waitlist form. Posts to the Pages function at /api/waitlist, which re-validates everything;
 * the browser checks here are for the user's convenience only.
 */
import { useState, type FormEvent } from 'react';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import MenuItem from '@mui/material/MenuItem';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import posthog from 'posthog-js';
import { LANGUAGES, MAX_COMPANIES, PAY_OPTIONS, ROLES } from './signup';

type Status = { kind: 'idle' | 'sending' | 'done' } | { kind: 'error'; message: string };

function Choice({ name, label, options }: { name: string; label: string; options: readonly string[] }) {
  return (
    <TextField select required name={name} label={label} defaultValue="">
      {options.map((option) => (
        <MenuItem key={option} value={option}>
          {option}
        </MenuItem>
      ))}
    </TextField>
  );
}

async function submitSignup(fields: Record<string, FormDataEntryValue>): Promise<string | null> {
  try {
    const response = await fetch('/api/waitlist', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(fields),
    });
    if (response.ok) return null;
    const body = (await response.json().catch(() => ({}))) as { error?: string };
    return body.error ?? 'Something went wrong. Please try again.';
  } catch {
    return 'Could not reach the server. Check your connection and try again.';
  }
}

export default function WaitlistForm() {
  const [status, setStatus] = useState<Status>({ kind: 'idle' });

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setStatus({ kind: 'sending' });
    const fields = Object.fromEntries(new FormData(event.currentTarget));
    const error = await submitSignup(fields);
    if (error) {
      setStatus({ kind: 'error', message: error });
      return;
    }
    // Answers only, never the email: the waitlist itself lives in KV.
    posthog.capture('waitlist_joined', { role: fields.role, language: fields.language, pay: fields.pay });
    setStatus({ kind: 'done' });
  }

  if (status.kind === 'done') {
    return <Alert severity="success">You are on the list. We will email you when Dryrun opens.</Alert>;
  }

  return (
    <Box component="form" onSubmit={handleSubmit}>
      <Stack spacing={2.5}>
        <TextField required type="email" name="email" label="Email" autoComplete="email" />
        <Choice name="role" label="Your role" options={ROLES} />
        <TextField
          name="companies"
          label="Target companies"
          placeholder="For example Google, Razorpay, Atlassian"
          slotProps={{ htmlInput: { maxLength: MAX_COMPANIES } }}
        />
        <Choice name="language" label="Language you interview in" options={LANGUAGES} />
        <Choice name="pay" label="What would you pay for it?" options={PAY_OPTIONS} />
        {/* Honeypot: hidden from people and screen readers, filled in by naive bots. */}
        <Box aria-hidden sx={{ position: 'absolute', left: '-10000px' }}>
          {/* A meaningless name so browser autofill and form-filler extensions leave it empty. */}
          <input name="hp" tabIndex={-1} autoComplete="off" />
        </Box>
        {status.kind === 'error' && <Alert severity="error">{status.message}</Alert>}
        <Button type="submit" variant="contained" size="large" disabled={status.kind === 'sending'}>
          {status.kind === 'sending' ? 'Joining…' : 'Join the waitlist'}
        </Button>
      </Stack>
    </Box>
  );
}
