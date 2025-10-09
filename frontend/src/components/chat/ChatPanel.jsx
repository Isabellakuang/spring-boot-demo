import React from 'react';
import MessageList from './MessageList';
import InputBox from './InputBox';
import ModeSelector from './ModeSelector';
import { useQuery } from '../../hooks/useQuery';
import EmptyState from '../common/EmptyState';
import { MessageSquare } from 'lucide-react';

const ChatPanel = () => {
  const { messages, isLoading, handleSubmit } = useQuery();

  return (
    <div className="flex flex-col h-full bg-gray-50 dark:bg-gray-900">
      {/* 模式选择器 */}
      <div className="flex-shrink-0 bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700">
        <ModeSelector />
      </div>

      {/* 消息列表 */}
      <div className="flex-1 overflow-hidden">
        {messages.length === 0 ? (
          <EmptyState
            icon={MessageSquare}
            title="Start a Conversation"
            description="Ask questions about your documents. The system will intelligently route your query to the best model."
          />
        ) : (
          <MessageList />
        )}
      </div>

      {/* 输入框 */}
      <div className="flex-shrink-0 bg-white dark:bg-gray-800 border-t border-gray-200 dark:border-gray-700">
        <InputBox onSubmit={handleSubmit} disabled={isLoading} />
      </div>
    </div>
  );
};

export default ChatPanel;
