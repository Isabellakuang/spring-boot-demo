import React, { useState } from 'react';
import { FileText, ChevronDown, ChevronUp } from 'lucide-react';
import { highlightText } from '../../utils/formatters';

/**
 * 来源引用项组件
 */
const SourceItem = ({ source, query }) => {
  const [isExpanded, setIsExpanded] = useState(false);

  return (
    <div className="border border-gray-200 rounded-lg overflow-hidden bg-white hover:border-primary-300 transition-colors">
      {/* 头部 */}
      <button
        onClick={() => setIsExpanded(!isExpanded)}
        className="w-full flex items-center gap-3 p-4 text-left hover:bg-gray-50 transition-colors"
      >
        <div className="flex-shrink-0">
          <div className="w-8 h-8 bg-primary-50 rounded-lg flex items-center justify-center">
            <FileText className="w-4 h-4 text-primary-600" />
          </div>
        </div>

        <div className="flex-1 min-w-0">
          <h4 className="text-sm font-medium text-gray-900 truncate">
            {source.documentName}
          </h4>
          <div className="flex items-center gap-2 mt-1">
            <span className="text-xs text-gray-500">
              相似度: {(source.similarity * 100).toFixed(1)}%
            </span>
            <span className="text-xs text-gray-400">•</span>
            <span className="text-xs text-gray-500">
              第 {source.chunkIndex + 1} 段
            </span>
          </div>
        </div>

        <div className="flex-shrink-0">
          {isExpanded ? (
            <ChevronUp className="w-5 h-5 text-gray-400" />
          ) : (
            <ChevronDown className="w-5 h-5 text-gray-400" />
          )}
        </div>
      </button>

      {/* 内容 */}
      {isExpanded && (
        <div className="px-4 pb-4 border-t border-gray-100">
          <div className="mt-3 text-sm text-gray-700 leading-relaxed">
            <div
              className="prose prose-sm max-w-none"
              dangerouslySetInnerHTML={{
                __html: highlightText(source.content, query)
              }}
            />
          </div>
        </div>
      )}
    </div>
  );
};

export default SourceItem;
