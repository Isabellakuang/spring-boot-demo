import React from 'react';
import { BookOpen, X } from 'lucide-react';
import SourceItem from './SourceItem';
import EmptyState from '../common/EmptyState';

/**
 * 来源引用面板组件
 */
const SourcePanel = ({ sources = [], query = '', onClose }) => {
  if (sources.length === 0) {
    return (
      <div className="h-full flex flex-col">
        {/* 头部 */}
        <div className="flex items-center justify-between p-4 border-b border-gray-200">
          <div className="flex items-center gap-2">
            <BookOpen className="w-5 h-5 text-primary-600" />
            <h3 className="text-lg font-semibold text-gray-900">来源引用</h3>
          </div>
          {onClose && (
            <button
              onClick={onClose}
              className="p-1 hover:bg-gray-100 rounded transition-colors"
            >
              <X className="w-5 h-5 text-gray-500" />
            </button>
          )}
        </div>

        {/* 空状态 */}
        <div className="flex-1 flex items-center justify-center">
          <EmptyState
            icon={BookOpen}
            title="暂无来源引用"
            description="发送问题后将显示相关文档来源"
          />
        </div>
      </div>
    );
  }

  return (
    <div className="h-full flex flex-col bg-gray-50">
      {/* 头部 */}
      <div className="flex items-center justify-between p-4 bg-white border-b border-gray-200">
        <div className="flex items-center gap-2">
          <BookOpen className="w-5 h-5 text-primary-600" />
          <h3 className="text-lg font-semibold text-gray-900">来源引用</h3>
          <span className="px-2 py-0.5 bg-primary-100 text-primary-700 text-xs font-medium rounded-full">
            {sources.length}
          </span>
        </div>
        {onClose && (
          <button
            onClick={onClose}
            className="p-1 hover:bg-gray-100 rounded transition-colors"
          >
            <X className="w-5 h-5 text-gray-500" />
          </button>
        )}
      </div>

      {/* 来源列表 */}
      <div className="flex-1 overflow-y-auto p-4">
        <div className="space-y-3">
          {sources.map((source, index) => (
            <SourceItem
              key={`${source.documentId}-${source.chunkIndex}-${index}`}
              source={source}
              query={query}
            />
          ))}
        </div>
      </div>

      {/* 底部提示 */}
      <div className="p-4 bg-white border-t border-gray-200">
        <p className="text-xs text-gray-500 text-center">
          来源按相似度排序，点击展开查看详细内容
        </p>
      </div>
    </div>
  );
};

export default SourcePanel;
