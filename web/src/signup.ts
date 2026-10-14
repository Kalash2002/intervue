/**
 * Waitlist signup shape, the choices the form offers, and the validation the Pages function applies.
 * Shared by the form and functions/api/waitlist.ts so both read one list; no DOM or Workers APIs here.
 */

export const ROLES = [
  'Final-year student',
  'Software engineer, 0 to 2 years',
  'Software engineer, 3 to 5 years',
  'Software engineer, 6+ years',
  'Other',
] as const;

export const LANGUAGES = ['Java', 'Python', 'JavaScript / TypeScript', 'Go', 'Other'] as const;

// "Would pay at ₹499" is the Phase 0 exit metric (PLAN.md section 10), so ₹499 is its own choice.
export const PAY_OPTIONS = [
  'I would not pay',
  '₹499 a month (about $6)',
  '₹999 a month (about $12)',
  '₹1,500 a month (about $19)',
  'More than ₹1,500 a month',
] as const;

export interface Signup {
  email: string;
  role: (typeof ROLES)[number];
  companies: string;
  language: (typeof LANGUAGES)[number];
  pay: (typeof PAY_OPTIONS)[number];
}

export type ParseResult = { kind: 'valid'; signup: Signup } | { kind: 'invalid'; error: string } | { kind: 'bot' };

const EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const MAX_EMAIL = 254;
export const MAX_COMPANIES = 200;

function text(input: Record<string, unknown>, key: string): string {
  const value = input[key];
  return typeof value === 'string' ? value.trim() : '';
}

function oneOf<T extends string>(choices: readonly T[], value: string): value is T {
  return (choices as readonly string[]).includes(value);
}

/** Validates an untrusted request body. `hp` is a honeypot field humans never see. */
export function parseSignup(input: unknown): ParseResult {
  if (typeof input !== 'object' || input === null) return { kind: 'invalid', error: 'Expected a JSON object.' };
  const body = input as Record<string, unknown>;
  if (text(body, 'hp') !== '') return { kind: 'bot' };

  const email = text(body, 'email').toLowerCase();
  const role = text(body, 'role');
  const companies = text(body, 'companies');
  const language = text(body, 'language');
  const pay = text(body, 'pay');

  if (email.length > MAX_EMAIL || !EMAIL.test(email)) return { kind: 'invalid', error: 'Enter a valid email.' };
  if (!oneOf(ROLES, role)) return { kind: 'invalid', error: 'Choose a role.' };
  if (companies.length > MAX_COMPANIES)
    return { kind: 'invalid', error: `Keep target companies under ${MAX_COMPANIES} characters.` };
  if (!oneOf(LANGUAGES, language)) return { kind: 'invalid', error: 'Choose a language.' };
  if (!oneOf(PAY_OPTIONS, pay)) return { kind: 'invalid', error: 'Choose what you would pay.' };

  return { kind: 'valid', signup: { email, role, companies, language, pay } };
}
