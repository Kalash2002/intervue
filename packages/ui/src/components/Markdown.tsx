// Briefs, hints and walkthroughs are pack content, so their HTML is sanitised even though
// markdown-it already escapes raw HTML with `html: false`. Links to in-app DSA questions
// (`dsa://<question-id>`) become chips; until the DSA section ships (Phase 1ad) a click says so.
import DOMPurify from 'dompurify';
import MarkdownIt from 'markdown-it';
import type { RenderRule } from 'markdown-it/lib/renderer.mjs';
import { useMemo, type MouseEvent } from 'react';
import { useApp } from '../app-context.ts';

const DSA = 'dsa://';
const markdown = new MarkdownIt({ html: false, linkify: true });

// Links never nest in CommonMark, so the next link_close is the chip's own.
const proxy: RenderRule = (tokens, idx, options, _env, self) => self.renderToken(tokens, idx, options);
const openDefault = markdown.renderer.rules.link_open ?? proxy;
const closeDefault = markdown.renderer.rules.link_close ?? proxy;
markdown.renderer.rules.link_open = (tokens, idx, options, env, self) => {
  const href = String(tokens[idx]?.attrGet('href') ?? '');
  if (!href.startsWith(DSA)) return openDefault(tokens, idx, options, env, self);
  const close = tokens.slice(idx + 1).find((t) => t.type === 'link_close');
  if (close) close.meta = { chip: true };
  const id = markdown.utils.escapeHtml(href.slice(DSA.length));
  return `<span class="dsa-chip" role="link" tabindex="0" data-id="${id}">`;
};
markdown.renderer.rules.link_close = (tokens, idx, options, env, self) =>
  tokens[idx]?.meta?.chip ? '</span>' : closeDefault(tokens, idx, options, env, self);

export function Markdown({ source, wide = false }: { source: string; wide?: boolean }) {
  const { notify } = useApp();
  const html = useMemo(() => DOMPurify.sanitize(markdown.render(source)), [source]);
  const onClick = (e: MouseEvent<HTMLDivElement>) => {
    if ((e.target as HTMLElement).closest('.dsa-chip')) notify('DSA arrives in the next release.');
  };
  return <div className={wide ? 'md wide' : 'md'} onClick={onClick} dangerouslySetInnerHTML={{ __html: html }} />;
}
