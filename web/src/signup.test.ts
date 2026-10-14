import { describe, expect, it } from 'vitest';
import { LANGUAGES, PAY_OPTIONS, ROLES, parseSignup } from './signup';

const valid = {
  email: '  Asha@Example.com ',
  role: ROLES[1],
  companies: ' Google, Flipkart ',
  language: LANGUAGES[0],
  pay: PAY_OPTIONS[1],
  hp: '',
};

describe('parseSignup', () => {
  it('acceptsAValidSignupAndNormalisesEmailAndWhitespace', () => {
    expect(parseSignup(valid)).toEqual({
      kind: 'valid',
      signup: {
        email: 'asha@example.com',
        role: ROLES[1],
        companies: 'Google, Flipkart',
        language: LANGUAGES[0],
        pay: PAY_OPTIONS[1],
      },
    });
  });

  it('allowsEmptyTargetCompanies', () => {
    expect(parseSignup({ ...valid, companies: undefined }).kind).toBe('valid');
  });

  it('flagsAFilledHoneypotAsBotBeforeAnyOtherCheck', () => {
    expect(parseSignup({ hp: 'http://spam.example' })).toEqual({ kind: 'bot' });
  });

  it.each([null, 'text', 42])('rejectsANonObjectBody %s', (body) => {
    expect(parseSignup(body).kind).toBe('invalid');
  });

  it.each([
    ['email', 'not-an-email'],
    ['email', `${'a'.repeat(250)}@x.io`],
    ['role', 'CEO'],
    ['companies', 'x'.repeat(201)],
    ['language', 'COBOL'],
    ['pay', '₹1'],
    ['pay', 42],
  ])('rejects %s = %s', (field, value) => {
    expect(parseSignup({ ...valid, [field]: value }).kind).toBe('invalid');
  });
});
