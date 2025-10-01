import type { Faq } from '../types/faq';

interface FaqCardProps {
  faq: Faq;
}

function FaqCard({ faq }: FaqCardProps) {
  return (
    <article className="rounded-2xl border border-slate-700/60 bg-slate-900/70 p-5 shadow-xl transition hover:-translate-y-1 hover:border-cyan-400/60">
      <h3 className="text-lg font-semibold text-slate-100">{faq.question}</h3>
      <p className="mt-2 text-sm text-slate-300">{faq.answer}</p>
      <div className="mt-3 flex flex-wrap gap-2">
        {faq.tags.map((tag) => (
          <span
            key={tag}
            className="rounded-full bg-cyan-500/10 px-3 py-1 text-xs font-medium text-cyan-300"
          >
            #{tag}
          </span>
        ))}
      </div>
    </article>
  );
}

export default FaqCard;