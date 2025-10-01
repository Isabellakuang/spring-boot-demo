import { useState } from 'react';
import Button from '../../../shared/components/Button';
import StatusMessage from '../../../shared/components/StatusMessage';
import type {
  ConversationView,
  MessageRequest
} from '../types/conversation';
import MessageBubble from './MessageBubble';

interface ConversationPanelProps {
  conversation?: ConversationView;
  loading: boolean;
  onRefresh: () => void;
  onReset: () => void;
  onSendMessage: (message: MessageRequest) => void;
}

function ConversationPanel({
  conversation,
  loading,
  onRefresh,
  onReset,
  onSendMessage
}: ConversationPanelProps) {
  const [draft, setDraft] = useState('');

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    if (!draft.trim()) return;
    onSendMessage({ sender: 'CUSTOMER', content: draft });
    setDraft('');
  };

  return (
    <div className="flex flex-col gap-4 rounded-3xl border border-slate-800 bg-slate-950/80 p-6 shadow-2xl">
      <div className="flex items-center justify-between gap-3">
        <div>
          <h2 className="text-2xl font-semibold text-slate-100">
            Conversation Center
          </h2>
          {conversation ? (
            <p className="text-xs text-slate-400">
              Conversation #{conversation.conversationId} ·{' '}
              {conversation.status}
            </p>
          ) : (
            <p className="text-xs text-slate-400">
              Start a conversation to see intelligent suggestions.
            </p>
          )}
        </div>
        <div className="flex gap-2">
          <Button type="button" variant="ghost" onClick={onRefresh} disabled={loading}>
            Refresh
          </Button>
          <Button type="button" variant="secondary" onClick={onReset}>
            New
          </Button>
        </div>
      </div>

      <div className="grid gap-3">
        <div className="max-h-[420px] overflow-y-auto rounded-2xl border border-slate-800/60 bg-slate-900/60 p-4">
          {loading && (
            <StatusMessage type="info" message="Refreshing conversation…" />
          )}

          {!loading && conversation && conversation.messages.length === 0 && (
            <StatusMessage
              type="info"
              message="No messages yet. Say hello!"
            />
          )}

          {!conversation && (
            <StatusMessage
              type="info"
              message="Create a conversation to view history and system suggestions."
            />
          )}

          {conversation?.messages.map((message, index) => (
            <MessageBubble key={`${message.timestamp}-${index}`} {...message} />
          ))}
        </div>

        <form onSubmit={handleSubmit} className="flex flex-col gap-3">
          <textarea
            placeholder="Type your message…"
            className="min-h-[100px] resize-y rounded-2xl border border-slate-700 bg-slate-950 px-3 py-2 text-sm text-slate-100 focus:border-cyan-400 focus:outline-none focus:ring-2 focus:ring-cyan-500"
            value={draft}
            onChange={(event) => setDraft(event.target.value)}
          />
          <div className="flex gap-3">
            <Button type="submit" disabled={loading || !draft.trim()}>
              Send
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default ConversationPanel;