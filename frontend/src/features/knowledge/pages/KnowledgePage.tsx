import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { fetchFaqs, searchFaq } from '../services/knowledgeApi';
import FaqCard from '../components/FaqCard';
import StatusMessage from '../../../shared/components/StatusMessage';
import Button from '../../../shared/components/Button';
import { useToast } from '../../../shared/hooks/useToast';
import { Faq } from '../types/faq';

function KnowledgePage() {
  const [query, setQuery] = useState('');
  const [suggestion, setSuggestion] = useState<Faq | null>(null);
  const { notify } = useToast();

  const {
    data: faqs = [],
    isLoading,
    error
  } = useQuery({
    queryKey: ['faqs'],
    queryFn: fetchFaqs
  });

  const handleSearch = async () => {
    if (!query.trim()) {
      setSuggestion(null);
      notify('Please enter a keyword to search.', 'info');
      return;
    }
    try {
      const result = await searchFaq(query);
      if (result) {
        setSuggestion(result);
      } else {
        setSuggestion(null);
        notify('No matching FAQ found. Try another keyword.', 'info');
      }
    } catch (err) {
      console.error(err);
      notify('Search failed. Please try again.', 'error');
    }
  };

  return (
    <section className="rounded-3xl border border-slate-800/80 bg-slate-950/80 p-6 shadow-2xl backdrop-blur">
      <h2 className="text-2xl font-semibold text-slate-100">Knowledge Base</h2>

      <div className="mt-4 flex gap-3">
        <input
          className="w-full rounded-2xl border border-slate-700 bg-slate-900 px-4 py-2 text-sm text-slate-100 outline-none transition focus:border-cyan-400 focus:ring-2 focus:ring-cyan-500"
          placeholder="Search FAQs (e.g. baggage, refund, flight)…"
          value={query}
          onChange={(event) => setQuery(event.target.value)}
        />
        <Button type="button" onClick={handleSearch}>
          Search
        </Button>
      </div>

      {isLoading && (
        <div className="mt-4">
          <StatusMessage type="info" message="Loading FAQs…" />
        </div>
      )}

      {error instanceof Error && (
        <div className="mt-4">
          <StatusMessage type="error" message={error.message} />
        </div>
      )}

      {suggestion && (
        <div className="mt-6 rounded-2xl border border-cyan-500/40 bg-cyan-500/10 p-4">
          <h3 className="text-lg font-semibold text-cyan-200">
            Suggested Answer
          </h3>
          <p className="mt-2 text-sm text-cyan-100">
            <strong>Q:</strong> {suggestion.question}
          </p>
          <p className="text-sm text-cyan-100">
            <strong>A:</strong> {suggestion.answer}
          </p>
        </div>
      )}

      <div className="mt-6 grid gap-4">
        {faqs.map((faq) => (
          <FaqCard key={faq.id} faq={faq} />
        ))}
      </div>
    </section>
  );
}

export default KnowledgePage;
