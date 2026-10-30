// Briefs, hints and walkthroughs are pack content, so their HTML is sanitised even though
// markdown-it already escapes raw HTML with `html: false`. Links to in-app DSA questions
// (`dsa://<question-id>`) become chips; a click or Enter opens the question, or says it is not written yet.
// Images are figures: their source comes from the `figures` map the engine inlines (an <img>
// cannot carry the API token), and a figure that is not in the map shows its caption alone.
import DOMPurify from 'dompurify';
import MarkdownIt from 'markdown-it';
import type { RenderRule } from 'markdown-it/lib/renderer.mjs';
import { useMemo, type KeyboardEvent, type MouseEvent } from 'react';
import { useOpenQuestion } from '../hooks.ts';

const DSA = 'dsa://';
const markdown = new MarkdownIt({ html: false, linkify: true });
const escape = markdown.utils.escapeHtml;

interface Env {
  figures?: Record<string, string>;
}

// Links never nest in CommonMark, so the next link_close is the chip's own.
const proxy: RenderRule = (tokens, idx, options, _env, self) => self.renderToken(tokens, idx, options);
const openDefault = markdown.renderer.rules.link_open ?? proxy;
const closeDefault = markdown.renderer.rules.link_close ?? proxy;
markdown.renderer.rules.link_open = (tokens, idx, options, env, self) => {
  const href = String(tokens[idx]?.attrGet('href') ?? '');
  if (!href.startsWith(DSA)) return openDefault(tokens, idx, options, env, self);
  const close = tokens.slice(idx + 1).find((t) => t.type === 'link_close');
  if (close) close.meta = { chip: true };
  return `<span class="dsa-chip" role="link" tabindex="0" data-id="${escape(href.slice(DSA.length))}">`;
};
markdown.renderer.rules.link_close = (tokens, idx, options, env, self) =>
  tokens[idx]?.meta?.chip ? '</span>' : closeDefault(tokens, idx, options, env, self);
markdown.renderer.rules.image = (tokens, idx, _options, env) => {
  const token = tokens[idx];
  const src = String(token?.attrGet('src') ?? '');
  const caption = escape(token?.content ?? '');
  const data = (env as Env).figures?.[src];
  return data
    ? `<figure class="fig"><img src="${data}" alt="${caption}"><figcaption>${caption}</figcaption></figure>`
    : `<figure class="fig missing"><figcaption>${caption}</figcaption></figure>`;
};

export function Markdown({
  source,
  wide = false,
  figures,
}: {
  source: string;
  wide?: boolean;
  /** Relative path in the Markdown to a data URI, as the walkthrough route returns them. */
  figures?: Record<string, string>;
}) {
  const openQuestion = useOpenQuestion();
  const html = useMemo(() => DOMPurify.sanitize(markdown.render(source, { figures } satisfies Env)), [source, figures]);
  const open = (e: MouseEvent | KeyboardEvent) => {
    const id = (e.target as HTMLElement).closest<HTMLElement>('.dsa-chip')?.dataset.id;
    if (id) void openQuestion(id);
  };
  const onKeyDown = (e: KeyboardEvent<HTMLDivElement>) => e.key === 'Enter' && open(e);
  return (
    <div
      className={wide ? 'md wide' : 'md'}
      onClick={open}
      onKeyDown={onKeyDown}
      dangerouslySetInnerHTML={{ __html: html }}
    />
  );
}
