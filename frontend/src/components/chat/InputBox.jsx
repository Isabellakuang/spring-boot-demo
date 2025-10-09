import React, { useState, useRef, useEffect } from 'react';
import { Send, Loader2, AlertCircle } from 'lucide-react';
import Button from '../common/Button';

/**
 * 聊天输入框组件
 * 支持多行输入、Enter发送、Shift+Enter换行
 */
const InputBox = ({ onSubmit, disabled = false, placeholder = '输入您的问题...' }) => {
  const [input, setInput] = useState('');
  const [isComposing, setIsComposing] = useState(false);
  const textareaRef = useRef(null);

  // 自动调整文本框高度
  useEffect(() => {
    if (textareaRef.current) {
      textareaRef.current.style.height = 'auto';
      textareaRef.current.style.height = `${Math.min(textareaRef.current.scrollHeight, 200)}px`;
    }
  }, [input]);

  // 处理提交
  const handleSubmit = () => {
    const trimmedInput = input.trim();
    if (!trimmedInput || disabled) return;

    onSubmit(trimmedInput);
    setInput('');
    
    // 重置文本框高度
    if (textareaRef.current) {
      textareaRef.current.style.height = 'auto';
    }
  };

  // 处理键盘事件
  const handleKeyDown = (e) => {
    // 中文输入法输入时不处理
    if (isComposing) return;

    // Enter发送，Shift+Enter换行
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSubmit();
    }
  };

  return (
    <div className="border-t border-gray-200 bg-white p-4">
      <div className="max-w-4xl mx-auto">
        <div className="flex items-end gap-2">
          {/* 输入框 */}
          <div className="flex-1 relative">
            <textarea
              ref={textareaRef}
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={handleKeyDown}
              onCompositionStart={() => setIsComposing(true)}
              onCompositionEnd={() => setIsComposing(false)}
              placeholder={placeholder}
              disabled={disabled}
              rows={1}
              className={`
                w-full px-4 py-3 pr-12
                border border-gray-300 rounded-lg
                resize-none overflow-y-auto
                focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent
                disabled:bg-gray-50 disabled:text-gray-500 disabled:cursor-not-allowed
                transition-all duration-200
              `}
              style={{ maxHeight: '200px' }}
            />
            
            {/* 字符计数 */}
            {input.length > 0 && (
              <div className="absolute bottom-2 right-2 text-xs text-gray-400">
                {input.length}
              </div>
            )}
          </div>

          {/* 发送按钮 */}
          <Button
            onClick={handleSubmit}
            disabled={disabled || !input.trim()}
            variant="primary"
            size="lg"
            className="px-6"
          >
            {disabled ? (
              <Loader2 className="w-5 h-5 animate-spin" />
            ) : (
              <Send className="w-5 h-5" />
            )}
          </Button>
        </div>

        {/* 提示信息 */}
        <div className="mt-2 flex items-center justify-between text-xs text-gray-500">
          <div className="flex items-center gap-4">
            <span>按 Enter 发送，Shift + Enter 换行</span>
            {disabled && (
              <span className="flex items-center gap-1 text-amber-600">
                <AlertCircle className="w-3 h-3" />
                正在处理中...
              </span>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default InputBox;
