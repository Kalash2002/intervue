# Palette: dark grey and dark purple

The token source for `packages/ui/src/styles/tokens.css` from Phase 1ac on. Supplied by the owner on 2026-10-15 from another app; the app maps these roles onto its existing token names (`--bg`, `--panel`, `--panel-2`, `--line`, `--text`, `--muted`, `--accent`, `--accent-ink`, `--accent-soft`, `--link`, `--select`, `--pass`, `--fail`, `--warn`) and adds only the roles it uses. Dark is the default theme.

## Core colours

| Role | Light | Dark |
|---|---|---|
| Background | #FCFAFF | #0B0A10 |
| Surface (cards) | #FFFFFF | #15121D |
| Raised surface | #F3EEF9 | #201A2B |
| Input field | #F0EBF6 | #251F30 |
| Text, primary | #21182B | #F5F1FA |
| Text, secondary | #62566F | #C2B8CF |
| Text, muted | #71617D | #A79BB7 |
| Main button (violet) / text on it | #6D28D9 / #FFFFFF | #7C3AED / #FFFFFF |
| Links, focus, selected controls | #6D28D9 | #C4B5FD |
| Selected container / its text | #EDE3FA / #32144F | #352345 / #F5F1FA |
| Pink accent (brand, coins, ratings only) / text on it | #A92C70 / #FFFFFF | #F0ABCF / #351426 |
| Outline (meaningful borders) | #82718F | #83748F |
| Divider (decorative) | #E5DCEA | #332B3E |
| Snackbar background / text | #201A2B / #F5F1FA | #F3EEF9 / #21182B |

## Chat bubbles (assistant panel)

| Role | Light | Dark |
|---|---|---|
| Incoming bubble / text | #F0EAF6 / #21182B | #211B2B / #F5F1FA |
| Outgoing bubble / text | #E6D8FA / #32144F | #4C287D / #FAF7FF |

In dark mode only, chat surfaces use a slightly cooler set:

| Chat role (dark) | Colour |
|---|---|
| Background | #09090D |
| Header, composer and surfaces | #14141C |
| Incoming bubble and input | #20202B |
| Outgoing bubble | #4C287D |
| Links | #A78BFA |
| Pink | #F472B6 |
| Text | #F5F5FA |
| Secondary text | #B8B5C7 |
| Divider | #302D3C |

## Status colours (text / background)

| Status | Light | Dark |
|---|---|---|
| Success / available / pass | #21663C / #E7F3E9 | #A0D9AF / #193625 |
| Warning / pending | #775000 / #FFF1CC | #E9C57B / #3B2E15 |
| Busy | #8A491C / #FFEBDD | #EDB88F / #40291C |
| Offline | #62566F / #F0EBF6 | #C2B8CF / #251F30 |
| Test mode (mock-payment labels) | #735018 / #F8EEDB | #DDC495 / #352D20 |
| Unread badge / text | #6D28D9 / #FFFFFF | #C4B5FD / #251047 |
| Error / text on it (fail) | #B32632 / #FFFFFF | #FFB4BC / #570C1B |
| Error container / its text | #FBE8EB / #801D2A | #491B26 / #FFD9DF |
| Destructive button / text | #B32632 / #FFFFFF | #B32632 / #FFFFFF |

## Rules

- Contrast: the main violet button on white is 7.1:1 and pink on white is 6.4:1. Text stays at 4.5:1 or better, meaningful outlines at 3:1. Check the syntax colours of `tokens.css` against the new dark background and re-tune any that fall under 4.5:1.
- Status is never shown by colour alone; always a word or an icon with it.
- Background washes (a faint drifting gradient) appear only on the Home page, at most 4.5% opacity in dark and 2.5% in light. Nowhere else.
- Pink is for brand marks and ratings only, never for actions or status.
