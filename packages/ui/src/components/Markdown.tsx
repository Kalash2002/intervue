// Briefs, hints and walkthroughs are pack content, so their HTML is sanitised even though
// markdown-it already escapes raw HTML with `html: false`.
import DOMPurify from 'dompurify';
import MarkdownIt from 'markdown-it';
import { useMemo } from 'react';

const markdown = new MarkdownIt({ html: false, linkify: true });

export function Markdown({ source, wide = false }: { source: string; wide?: boolean }) {
  const html = useMemo(() => DOMPurify.sanitize(markdown.render(source)), [source]);
  return <div className={wide ? 'md wide' : 'md'} dangerouslySetInnerHTML={{ __html: html }} />;
}
