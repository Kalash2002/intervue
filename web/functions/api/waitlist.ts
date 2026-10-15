/**
 * POST /api/waitlist: validates a signup and stores it in the WAITLIST KV namespace, one key per email.
 * Holds no rules of its own; validation lives in src/signup.ts.
 */
import { parseSignup } from '../../src/signup';

interface Env {
  WAITLIST: KVNamespace;
}

const MAX_BODY_CHARS = 4096;

const tooLarge = () => Response.json({ error: 'Request too large.' }, { status: 413 });

export const onRequestPost: PagesFunction<Env> = async ({ request, env }) => {
  // Content-Length rejects honest large posts before reading; the length check covers chunked bodies.
  if (Number(request.headers.get('content-length') ?? 0) > MAX_BODY_CHARS) return tooLarge();
  const raw = await request.text();
  if (raw.length > MAX_BODY_CHARS) return tooLarge();

  let body: unknown = null;
  try {
    body = JSON.parse(raw);
  } catch {
    // Left as null so parseSignup reports the bad body with its usual message.
  }

  const result = parseSignup(body);
  // Bots get the same answer as people so the honeypot is not revealed.
  if (result.kind === 'bot') return Response.json({ ok: true });
  if (result.kind === 'invalid') return Response.json({ error: result.error }, { status: 400 });

  // Keyed by email so a resubmission updates the entry instead of inflating the count.
  const entry = { ...result.signup, country: request.cf?.country ?? null, createdAt: new Date().toISOString() };
  try {
    await env.WAITLIST.put(`signup:${result.signup.email}`, JSON.stringify(entry));
  } catch {
    // KV refuses writes over the daily quota or more than once a second per key; ask the person to retry
    // rather than claim success for a signup that was not stored.
    return Response.json({ error: 'We could not save that just now. Please try again in a minute.' }, { status: 503 });
  }
  return Response.json({ ok: true });
};
