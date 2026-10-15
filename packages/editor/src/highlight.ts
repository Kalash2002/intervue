// Syntax colours read from the theme tokens (--syn-* in packages/ui tokens.css), so one style serves
// both themes. CodeMirror's default style is made for light backgrounds and glared on the dark one.
import { HighlightStyle, syntaxHighlighting } from '@codemirror/language';
import { tags as t } from '@lezer/highlight';

export const syntaxColours = syntaxHighlighting(
  HighlightStyle.define([
    { tag: t.keyword, color: 'var(--syn-keyword)' },
    { tag: [t.typeName, t.className], color: 'var(--syn-type)' },
    { tag: [t.string, t.character], color: 'var(--syn-string)' },
    { tag: [t.number, t.bool, t.null, t.atom], color: 'var(--syn-number)' },
    {
      tag: [t.function(t.variableName), t.function(t.propertyName), t.definition(t.variableName)],
      color: 'var(--syn-def)',
    },
    { tag: t.comment, color: 'var(--muted)', fontStyle: 'italic' },
    { tag: [t.annotation, t.meta], color: 'var(--syn-meta)' },
    { tag: t.invalid, color: 'var(--fail)' },
  ]),
);
