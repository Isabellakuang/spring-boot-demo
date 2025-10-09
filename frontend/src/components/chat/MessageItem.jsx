import React from 'react';
import { User, Bot, Copy, Check } from 'lucide-react';
import { formatDateTime } from '../../utils/formatters';
import SourceReferences from '../source/SourceReferences';
import { useState } from 'react';

const MessageItem = ({ message }) => {
  const [copied, setCopied] = useState(false);
  const isUser = message.role === 'user';

  const handleCopy = async () => {
    await navigator.clipboard.writeText(message.content);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div className={`flex ${isUser ? 'justify-end' : 'justify-start'}`}>
      <div className={`flex gap-3 max-w-3xl ${isUser ? 'flex-row-reverse' : 'flex-row'}`}>
        {/* Avatar */}
        <div className={`
          flex-shrink-0 w-8 h-8 rounded-full flex items-center justify-center
          ${isUser 
            ? 'bg-blue-600 dark:bg-blue-500' 
            : 'bg-gray-200 dark:bg-gray-700'
          }
        `}>
          {isUser ? (
            <User className="w-5 h-5 text-white" />
          ) : (
            <Bot className="w-5 h-5 text-gray-700 dark:text-gray-300" />
          )}
        </div>

        {/* Message Content */}
        <div className={`flex-1 ${isUser ? 'items-end' : 'items-start'} flex flex-col`}>
          <div className={`
            rounded-lg p-4 shadow-sm border
            ${isUser 
              ? 'bg-blue-600 dark:bg-blue-500 text-white border-blue-600 dark:border-blue-500' 
              : 'bg-white dark:bg-gray-800 text-gray-900 dark:text-white border-gray-200 dark:border-gray-700'
            }
          `}>
            {/* Message Text */}
            <div className="prose prose-sm dark:prose-invert max-w-none">
              <div className="whitespace-pre-wrap break-words">{message.content}</div>
            </div>

            {/* Mode Badge */}
            {!isUser && message.mode && (
              <div className="mt-2 pt-2 border-t border-gray-200 dark:border-gray-700">
                <span className="inline-flex items-center px-2 py-1 rounded text-xs font-medium bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300">
                  Mode: {message.mode.toUpperCase()}
                </span>
              </div>
            )}

            {/* Copy Button */}
            {!isUser && (
              <button
                onClick={handleCopy}
                className="mt-2 p-1 rounded hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
                title="Copy message"
              >
                {copied ? (
                  <Check className="w-4 h-4 text-green-600 dark:text-green-400" />
                ) : (
                  <Copy className="w-4 h-4 text-gray-500 dark:text-gray-400" />
                )}
              </button>
            )}
          </div>

          {/* Source References */}
          {!isUser && message.sources && message.sources.length > 0 && (
            <div className="mt-2 w-full">
              <SourceReferences sources={message.sources} />
            </div>
          )}

          {/* Timestamp */}
          <div className={`mt-1 text-xs text-gray-500 dark:text-gray-400 ${isUser ? 'text-right' : 'text-left'}`}>
            {formatDateTime(message.timestamp || new Date())}
          </div>
        </div>
      </div>
    </div>
  );
};

export default MessageItem;
