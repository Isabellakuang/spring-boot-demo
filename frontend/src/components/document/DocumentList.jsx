import React from 'react';
import { FileText, RefreshCw } from 'lucide-react';
import DocumentItem from './DocumentItem';
import EmptyState from '../common/EmptyState';
import Loading from '../common/Loading';
import Button from '../common/Button';

/**
 * 文档列表组件
 */
const DocumentList = ({ 
  documents = [], 
  loading = false, 
  onDelete, 
  onRefresh 
}) => {
  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <Loading text="加载文档列表..." />
      </div>
    );
  }

  if (documents.length === 0) {
    return (
      <EmptyState
        icon={FileText}
        title="暂无文档"
        description="上传PDF文档开始使用RAG问答系统"
      />
    );
  }

  return (
    <div className="space-y-4">
      {/* 列表头部 */}
      <div className="flex items-center justify-between">
        <div className="text-sm text-gray-600">
          共 <span className="font-semibold text-gray-900">{documents.length}</span> 个文档
        </div>
        <Button
          onClick={onRefresh}
          variant="ghost"
          size="sm"
          className="gap-2"
        >
          <RefreshCw className="w-4 h-4" />
          刷新
        </Button>
      </div>

      {/* 文档列表 */}
      <div className="space-y-2">
        {documents.map((document) => (
          <DocumentItem
            key={document.id}
            document={document}
            onDelete={onDelete}
          />
        ))}
      </div>
    </div>
  );
};

export default DocumentList;
