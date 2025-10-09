import React, { useEffect, useRef } from 'react';
import useQueryStore from '../../store/queryStore';
import MessageItem from './MessageItem';
import Loading from '../common/Loading';

const MessageList = () => {
  const { messages, loading } = useQueryStore();
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  return (
    <div className="h-full overflow-y-auto px-4 py-6 space-y-6">
      {messages.map((message, index) => (
        <MessageItem key={message.id || index} message={message} />
      ))}
      
      {loading && (
        <div className="flex justify-start">
          <div className="bg-white dark:bg-gray-800 rounded-lg p-4 shadow-sm border border-gray-200 dark:border-gray-700">
            <Loading size="sm" text="Thinking..." />
          </div>
        </div>
      )}
      
      <div ref={messagesEndRef} />
    </div>
  );
};

export default MessageList;
