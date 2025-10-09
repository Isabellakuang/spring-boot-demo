import React from 'react';
import { FileText, Trash2, Clock, CheckCircle, AlertCircle, Loader2 } from 'lucide-react';
import { formatFileSize, formatRelativeTime } from '../../utils/formatters';
import Button from '../common/Button';

/**
 * 文档列表项组件
 */
const DocumentItem = ({ document, onDelete }) => {
  const getStatusIcon = (status) => {
    switch (status) {
      case 'PROCESSED':
        return <CheckCircle className="w-4 h-4 text-green-500" />;
      case 'PROCESSING':
        return <Loader2 className="w-4 h-4 text-blue-500 animate-spin" />;
      case 'FAILED':
        return <AlertCircle className="w-4 h-4 text-red-500" />;
      default:
        return <Clock className="w-4 h-4 text-gray-400" />;
    }
  };

  const getStatusText = (status) => {
    switch (status) {
      case 'PROCESSED':
        return '已处理';
      case 'PROCESSING':
        return '处理中';
      case 'FAILED':
        return '处理失败';
      default:
        return '待处理';
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'PROCESSED':
        return 'text-green-600 bg-green-50';
      case 'PROCESSING':
        return 'text-blue-600 bg-blue-50';
      case 'FAILED':
        return 'text-red-600 bg-red-50';
      default:
        return 'text-gray-600 bg-gray-50';
    }
  };

  return (
    <div className="group flex items-center gap-3 p-4 bg-white border border-gray-200 rounded-lg hover:border-primary-300 hover:shadow-sm transition-all duration-200">
      {/* 文件图标 */}
      <div className="flex-shrink-0">
        <div className="w-10 h-10 bg-primary-50 rounded-lg flex items-center justify-center">
          <FileText className="w-5 h-5 text-primary-600" />
        </div>
      </div>

      {/* 文件信息 */}
      <div className="flex-1 min-w-0">
        <div className="flex items-center gap-2 mb-1">
          <h4 className="text-sm font-medium text-gray-900 truncate">
            {document.fileName}
          </h4>
          <span className={`inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-medium ${getStatusColor(document.status)}`}>
            {getStatusIcon(document.status)}
            {getStatusText(document.status)}
          </span>
        </div>
        
        <div className="flex items-center gap-3 text-xs text-gray-500">
          <span>{formatFileSize(document.fileSize)}</span>
          <span>•</span>
          <span>{document.chunkCount || 0} 个片段</span>
          <span>•</span>
          <span>{formatRelativeTime(document.uploadedAt)}</span>
        </div>
      </div>

      {/* 操作按钮 */}
      <div className="flex-shrink-0 opacity-0 group-hover:opacity-100 transition-opacity duration-200">
        <Button
          onClick={() => onDelete(document.id)}
          variant="ghost"
          size="sm"
          className="text-red-600 hover:text-red-700 hover:bg-red-50"
          disabled={document.status === 'PROCESSING'}
        >
          <Trash2 className="w-4 h-4" />
        </Button>
      </div>
    </div>
  );
};

export default DocumentItem;
