import React from 'react';
import SourceItem from './SourceItem';
import { FileSearch } from 'lucide-react';

/**
 * 源引用列表组件
 * 显示消息的所有源引用
 */
const SourceReferences = ({ sources, query = '' }) => {
  if (!sources || sources.length === 0) {
    return null;
  }

  return (
    <div className="space-y-2">
      <div className="flex items-center gap-2 text-sm font-medium text-gray-700 dark:text-gray-300">
        <FileSearch className="w-4 h-4" />
        <span>Sources ({sources.length})</span>
      </div>
      <div className="space-y-2">
        {sources.map((source, index) => (
          <SourceItem
            key={`${source.documentId}-${source.chunkIndex}-${index}`}
            source={source}
            query={query}
          />
        ))}
      </div>
    </div>
  );
};

export default SourceReferences;
