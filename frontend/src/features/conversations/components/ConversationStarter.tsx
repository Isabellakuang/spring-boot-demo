import { useState } from 'react';
import Button from '../../../shared/components/Button';
import type { CreateConversationRequest } from '../types/conversation';

interface ConversationStarterProps {
  onSubmit: (payload: CreateConversationRequest) => void;
  loading: boolean;
}

const defaultForm: CreateConversationRequest = {
  subject: '',
  customerEmail: '',
  initialMessage: ''
};

function ConversationStarter({ onSubmit, loading }: ConversationStarterProps) {
  const [formValues, setFormValues] = useState<CreateConversationRequest>(
    defaultForm
  );

  const handleChange = (
    event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = event.target;
    setFormValues((current) => ({ ...current, [name]: value }));
  };

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    if (loading) return;
    onSubmit(formValues);
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="grid gap-4 rounded-2xl border border-slate-800 bg-slate-900/80 p-5"
    >
      <label className="grid gap-2 text-sm font-semibold text-slate-200">
        Subject
        <input
          name="subject"
          type="text"
          required
          value={formValues.subject}
          onChange={handleChange}
          placeholder="Flight change inquiry"
          className="rounded-xl border border-slate-700 bg-slate-950 px-3 py-2 text-sm text-slate-100 focus:border-cyan-400 focus:outline-none focus:ring-2 focus:ring-cyan-500"
        />
      </label>

      <label className="grid gap-2 text-sm font-semibold text-slate-200">
        Customer Email
        <input
          name="customerEmail"
          type="email"
          required
          value={formValues.customerEmail}
          onChange={handleChange}
          placeholder="user@example.com"
          className="rounded-xl border border-slate-700 bg-slate-950 px-3 py-2 text-sm text-slate-100 focus:border-cyan-400 focus:outline-none focus:ring-2 focus:ring-cyan-500"
        />
      </label>

      <label className="grid gap-2 text-sm font-semibold text-slate-200">
        Initial Message
        <textarea
          name="initialMessage"
          required
          rows={4}
          value={formValues.initialMessage}
          onChange={handleChange}
          placeholder="I need to change my flight next week…"
          className="resize-y rounded-xl border border-slate-700 bg-slate-950 px-3 py-2 text-sm text-slate-100 focus:border-cyan-400 focus:outline-none focus:ring-2 focus:ring-cyan-500"
        />
      </label>

      <Button type="submit" disabled={loading}>
        {loading ? 'Creating…' : 'Start Conversation'}
      </Button>
    </form>
  );
}

export default ConversationStarter;