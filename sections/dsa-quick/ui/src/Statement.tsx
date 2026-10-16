// Lays a DSA statement out the way an interview site does: title and description, boxed examples
// with aligned Input/Output/Explanation labels, then Constraints and the optional Follow-up. The
// shape is owned by content/templates/dsa-statement.md; text that does not match it renders as
// plain Markdown. Blocks are React elements, so only text segments ever reach Markdown.
import { Markdown } from '@intervue/ui';

interface Section {
  heading: string;
  body: string;
}

const LABELS = ['Input', 'Output', 'Explanation'] as const;
const LABEL_LINE = /^\*\*(Input|Output|Explanation):\*\*\s*(.*)$/;

/** Splits on `## ` headings: the text before the first is the lead (title and description). */
function split(source: string): { lead: string; sections: Section[] } {
  const [lead = '', ...parts] = source.split(/^## /m);
  const sections = parts.map((part) => {
    const nl = part.indexOf('\n');
    return nl < 0
      ? { heading: part.trim(), body: '' }
      : { heading: part.slice(0, nl).trim(), body: part.slice(nl + 1).trim() };
  });
  return { lead: lead.trim(), sections };
}

/** The labelled lines of one example body; a line that is not labelled continues the previous one. */
function fields(body: string): Record<string, string> {
  const out: Record<string, string> = {};
  let last = '';
  for (const line of body.split('\n')) {
    const m = LABEL_LINE.exec(line);
    if (m?.[1]) {
      last = m[1];
      out[last] = m[2] ?? '';
    } else if (last && line.trim()) {
      out[last] += ` ${line.trim()}`;
    }
  }
  return out;
}

const SINGLE_SPAN = /^`([^`]+)`$/;

/** One code span is shown as is; several spans (and the text between them) go through inline Markdown. */
function Value({ text }: { text: string }) {
  const single = SINGLE_SPAN.exec(text);
  return single ? <code>{single[1]}</code> : <Markdown source={text} />;
}

function Example({ name, body }: { name: string; body: string }) {
  const values = fields(body);
  return (
    <section className="example" aria-label={name}>
      <h3 className="example-name">{name}</h3>
      <dl>
        {LABELS.filter((label) => values[label] !== undefined).map((label) => (
          <div className="example-row" key={label}>
            <dt>{label}</dt>
            <dd>
              {label === 'Explanation' ? (
                <Markdown source={values[label] ?? ''} />
              ) : (
                <Value text={values[label] ?? ''} />
              )}
            </dd>
          </div>
        ))}
      </dl>
    </section>
  );
}

function Titled({ heading, body }: Section) {
  return (
    <section className="stmt-part" aria-label={heading}>
      <h3 className="stmt-heading">{heading}</h3>
      <Markdown source={body} />
    </section>
  );
}

export function Statement({ source }: { source: string }) {
  const { lead, sections } = split(source);
  if (!sections.some((s) => s.heading === 'Example 1')) return <Markdown source={source} />;
  return (
    <div className="stmt">
      <Markdown source={lead} />
      {sections.map((s) =>
        /^Example \d+$/.test(s.heading) ? (
          <Example key={s.heading} name={s.heading} body={s.body} />
        ) : (
          <Titled key={s.heading} {...s} />
        ),
      )}
    </div>
  );
}
