// Lives outside functions/ because Pages turns every file there into a route.
import { describe, expect, it } from 'vitest';
import { onRequestPost } from '../functions/api/waitlist';

const valid = {
  email: 'Asha@Example.com',
  role: 'Final-year student',
  companies: '',
  language: 'Java',
  pay: '₹499 a month (about $6)',
  hp: '',
};

function fakeKv(failWith?: Error) {
  const store = new Map<string, string>();
  const put = async (key: string, value: string) => {
    if (failWith) throw failWith;
    store.set(key, value);
  };
  return { store, kv: { put } as unknown as KVNamespace };
}

async function post(body: string, kv: KVNamespace, headers: Record<string, string> = {}) {
  const request = new Request('https://dryrun.test/api/waitlist', { method: 'POST', body, headers });
  const context = { request, env: { WAITLIST: kv } } as unknown as Parameters<typeof onRequestPost>[0];
  const response = await onRequestPost(context);
  return { status: response.status, json: (await response.json()) as Record<string, unknown> };
}

describe('POST /api/waitlist', () => {
  it('storesAValidSignupUnderItsLowercasedEmail', async () => {
    const { store, kv } = fakeKv();
    expect(await post(JSON.stringify(valid), kv)).toEqual({ status: 200, json: { ok: true } });
    const saved = JSON.parse(store.get('signup:asha@example.com') ?? '{}') as Record<string, unknown>;
    expect(saved).toMatchObject({ email: 'asha@example.com', pay: valid.pay, country: null });
    expect(typeof saved.createdAt).toBe('string');
  });

  it('answersBotsLikePeopleButStoresNothing', async () => {
    const { store, kv } = fakeKv();
    expect(await post(JSON.stringify({ ...valid, hp: 'x' }), kv)).toEqual({ status: 200, json: { ok: true } });
    expect(store.size).toBe(0);
  });

  it.each(['not json', JSON.stringify({ ...valid, role: 'CEO' })])('rejectsABadBody %s', async (body) => {
    const { store, kv } = fakeKv();
    expect((await post(body, kv)).status).toBe(400);
    expect(store.size).toBe(0);
  });

  it('rejectsALargeBodyByContentLengthAndByActualLength', async () => {
    const { kv } = fakeKv();
    expect((await post('{}', kv, { 'content-length': '5000' })).status).toBe(413);
    expect((await post('a'.repeat(5000), kv)).status).toBe(413);
  });

  it('reportsAFailedKvWriteInsteadOfClaimingSuccess', async () => {
    const { kv } = fakeKv(new Error('KV PUT failed: 429 Too Many Requests'));
    expect((await post(JSON.stringify(valid), kv)).status).toBe(503);
  });
});
