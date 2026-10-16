/**
 * The room pieces two sections share: the code editor, output pane, splitter, assistant panel,
 * hint control and the hooks behind them (workspace files, room actions, chat).
 *
 * Must not hold anything section-specific (file tree, phase chips, guide panel, layout of a
 * particular room): a section composes these pieces itself.
 */
export { AssistantPanel } from './AssistantPanel.tsx';
export { CodeEditor, type CodeEditorProps } from './CodeEditor.tsx';
export { EndDialog } from './EndDialog.tsx';
export { HintControl } from './HintControl.tsx';
export { OutputPane, phaseTargets, targets } from './OutputPane.tsx';
export { RoomClock } from './RoomClock.tsx';
export { ScoreLine, totalHints, WalkthroughTab } from './ResultParts.tsx';
export { SIZES, Splitter } from './Splitter.tsx';
export { useChat, type ShownTurn } from './useChat.ts';
export { useRoomActions, type OutputBlock, type RoomActions } from './useRoomActions.ts';
export { useWorkspace, type Doc, type Workspace } from './useWorkspace.ts';
