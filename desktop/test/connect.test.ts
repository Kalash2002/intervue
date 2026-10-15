import { describe, expect, it, vi } from 'vitest';
import { connect } from '../src/connect.ts';

describe('connecting to the engine', () => {
  it('asks the shell for the port and token', async () => {
    const invoke = vi.fn(async () => ({ port: 51234, token: 'secret' }));

    const connection = await connect({ isTauri: () => true, invoke });

    expect(invoke).toHaveBeenCalledWith('engine_connection');
    expect(connection).toEqual({ baseUrl: 'http://127.0.0.1:51234', token: 'secret' });
  });

  it('uses the dev proxy without a token in a plain browser', async () => {
    const invoke = vi.fn();

    expect(await connect({ isTauri: () => false, invoke })).toEqual({ baseUrl: '/engine' });
    expect(invoke).not.toHaveBeenCalled();
  });

  it('passes on why the engine did not start', async () => {
    const invoke = vi.fn(async () => Promise.reject('The engine is missing: engine.jar'));

    await expect(connect({ isTauri: () => true, invoke })).rejects.toBe('The engine is missing: engine.jar');
  });
});
