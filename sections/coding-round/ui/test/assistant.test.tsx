import { fireEvent, screen, waitFor } from '@testing-library/react';
import type { EditReply, EngineClient } from '@intervue/local-engine-client';
import { describe, expect, it, vi } from 'vitest';
import { AssistantPanel } from '../src/room/AssistantPanel.tsx';
import type { RoomActions } from '../src/room/useRoomActions.ts';
import { event, renderWith, view } from './fixtures.tsx';

const NO_ROUTE = 'Assistant unavailable: sign-in arrives in Phase 1b';

function client(over: Partial<EngineClient> = {}): Partial<EngineClient> {
  return {
    aiStatus: vi.fn(async () => ({ developer: true, available: true, reason: null })),
    chat: vi.fn(async () => []),
    session: vi.fn(async () => view()),
    fakeEditIds: vi.fn(async () => ['hold-exposure-phase1']),
    ask: vi.fn(async (_id: string, _m: string, _r: string, onDelta: (t: string) => void) => {
      onDelta('Look at ');
      onDelta('line 15.');
      return 'Look at line 15.';
    }),
    ...over,
  };
}

function actions(over: Partial<RoomActions> = {}): RoomActions {
  return {
    busy: false,
    reply: null,
    writeCode: vi.fn(async () => {}),
    saveFiles: vi.fn(async () => {}),
    undo: vi.fn(),
    replay: vi.fn(),
    ...over,
  } as unknown as RoomActions;
}

const type = (text: string) =>
  fireEvent.change(screen.getByRole('textbox', { name: 'Message' }), { target: { value: text } });

describe('the assistant panel', () => {
  it('shows the reason and no controls when the route is off', async () => {
    renderWith(
      <AssistantPanel view={view()} actions={actions()} onChange={vi.fn()} />,
      client({ aiStatus: vi.fn(async () => ({ developer: false, available: false, reason: NO_ROUTE })) }),
    );

    expect(await screen.findByText(NO_ROUTE)).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Ask' })).toBeNull();
    expect(screen.queryByRole('button', { name: 'Write code' })).toBeNull();
    expect(screen.queryByRole('button', { name: 'Replay' })).toBeNull();
  });

  it('saves the open files before Ask, so the prompt has the code on screen', async () => {
    const order: string[] = [];
    const c = client({ ask: vi.fn(async () => (order.push('ask'), 'ok')) });
    const saveFiles = vi.fn(async () => void order.push('save'));
    renderWith(<AssistantPanel view={view()} actions={actions({ saveFiles })} onChange={vi.fn()} />, c);

    await screen.findByRole('button', { name: 'Ask' });
    type('is the visited check right?');
    fireEvent.click(screen.getByRole('button', { name: 'Ask' }));

    await waitFor(() => expect(order).toEqual(['save', 'ask']));
  });

  it('streams an Ask reply into the chat log', async () => {
    const c = client();
    renderWith(<AssistantPanel view={view()} actions={actions()} onChange={vi.fn()} />, c);

    await screen.findByRole('button', { name: 'Ask' });
    type('fix it');
    fireEvent.click(screen.getByRole('button', { name: 'Ask' }));

    // The streamed text is replaced by its rendered Markdown when the call ends.
    await waitFor(() => expect(screen.getByText('Look at line 15.')).toBeInTheDocument());
    expect(screen.getByText('fix it')).toBeInTheDocument();
    expect(c.ask).toHaveBeenCalledWith('s1', 'fix it', 'interview', expect.any(Function));
  });

  it('lets Practice choose the rules, Helpful by default', async () => {
    const c = client();
    renderWith(<AssistantPanel view={view({ type: 'PRACTICE' })} actions={actions()} onChange={vi.fn()} />, c);

    await screen.findByRole('button', { name: 'Ask' });
    type('hi');
    fireEvent.click(screen.getByRole('button', { name: 'Ask' }));
    await waitFor(() => expect(c.ask).toHaveBeenLastCalledWith('s1', 'hi', 'helpful', expect.any(Function)));

    fireEvent.change(screen.getByRole('combobox', { name: 'Rules' }), { target: { value: 'interview' } });
    type('again');
    fireEvent.click(screen.getByRole('button', { name: 'Ask' }));
    await waitFor(() => expect(c.ask).toHaveBeenLastCalledWith('s1', 'again', 'interview', expect.any(Function)));
  });

  it('offers no rules switch in an Interview session', async () => {
    renderWith(<AssistantPanel view={view()} actions={actions()} onChange={vi.fn()} />, client());

    await screen.findByRole('button', { name: 'Ask' });
    expect(screen.queryByRole('combobox', { name: 'Rules' })).toBeNull();
  });

  it('shows the Write code note, each outcome and an Undo per applied edit', async () => {
    const reply: EditReply = {
      replyId: 'w1',
      note: 'Widened the total.',
      outcomes: [
        {
          id: 'w1:e1',
          replyId: 'w1',
          path: 'Calc.java',
          op: 'replace',
          status: 'APPLIED',
          message: '',
          hunks: 1,
          beforeHash: 'a',
          afterHash: 'b',
        },
        {
          id: 'w1:e2',
          replyId: 'w1',
          path: 'Main.java',
          op: 'replace',
          status: 'SUGGESTION',
          message: 'changed meanwhile',
          hunks: 0,
          beforeHash: null,
          afterHash: null,
        },
      ],
    };
    const a = actions({ reply, writeCode: vi.fn(async () => reply) });
    renderWith(<AssistantPanel view={view()} actions={a} onChange={vi.fn()} />, client());

    await screen.findByRole('button', { name: 'Write code' });
    type('widen it in twice');
    fireEvent.click(screen.getByRole('button', { name: 'Write code' }));
    await waitFor(() => expect(a.writeCode).toHaveBeenCalledWith('widen it in twice', 'interview'));

    expect(screen.getByText('Widened the total.')).toBeInTheDocument();
    fireEvent.click(screen.getByRole('button', { name: 'Undo w1:e1' }));
    expect(a.undo).toHaveBeenCalledWith('w1:e1');
    expect(screen.queryByRole('button', { name: 'Undo w1:e2' })).toBeNull();
  });

  it('counts the Write code calls of this session', async () => {
    const v = view({
      events: [
        event(1, 'aiCall', 1, { kind: 'edit' }),
        event(2, 'aiCall', 2, { kind: 'chat' }),
        event(3, 'aiCall', 3, { kind: 'edit' }),
      ],
    });
    renderWith(<AssistantPanel view={v} actions={actions()} onChange={vi.fn()} />, client());

    expect(await screen.findByText('Write code calls this session: 2')).toBeInTheDocument();
  });

  it('shows the recorded-reply demo only in developer mode', async () => {
    renderWith(
      <AssistantPanel view={view()} actions={actions()} onChange={vi.fn()} />,
      client({ aiStatus: vi.fn(async () => ({ developer: false, available: true, reason: null })) }),
    );

    await screen.findByRole('button', { name: 'Ask' });
    expect(screen.queryByRole('button', { name: 'Replay' })).toBeNull();
  });
});
