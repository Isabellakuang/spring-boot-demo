import type { ConversationMessage } from '../types/conversation';

function MessageBubble({ sender, content, timestamp }: ConversationMessage) {
  const isCustomer = sender === 'CUSTOMER';
  const isSystem = sender === 'SYSTEM';

  const bubbleClasses = [
    'w-fit max-w-[80%] rounded-2xl border px-4 py-3 text-sm leading-relaxed shadow-lg transition',
    isCustomer
      ? 'ml-auto border-indigo-500/50 bg-indigo-500/20 text-indigo-50'
      : 'mr-auto border-slate-700/50 bg-slate-800/70 text-slate-100'
  ].join(' ');

  return (
    <div className="space-y-1">
      <div className="text-xs font-semibold uppercase tracking-wide text-slate-400">
        {sender} · {new Date(timestamp).toLocaleString()}
      </div>
      <div className={bubbleClasses}>
        <pre className="whitespace-pre-wrap font-sans text-sm">{content}</pre>
      </div>
      {isSystem && (
        <div className="text-[11px] text-cyan-300/80">Auto-suggested reply</div>
      )}
    </div>
  );
}

export default MessageBubble;