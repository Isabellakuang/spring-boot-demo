import React, { useEffect } from 'react';
import { Upload } from 'lucide-react';
import DocumentList from './DocumentList';
import Button from '../common/Button';
import useDocumentStore from '../../store/documentStore';
import useUIStore from '../../store/uiStore';

/**
 * 文档管理页面
 */
const DocumentsPage = () => {
  const { documents, loading, fetchDocuments, deleteDocument } = useDocumentStore();
  const { openModal } = useUIStore();

  useEffect(() => {
    fetchDocuments();
  }, [fetchDocuments]);

  const handleDelete = async (documentId) => {
    await deleteDocument(documentId);
  };

  const handleRefresh = () => {
    fetchDocuments();
  };

  return (
    <div className="h-full flex flex-col bg-gray-50 dark:bg-gray-900">
      {/* 页面头部 */}
      <div className="bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700 px-6 py-4">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900 dark:text-white">
              文档管理
            </h1>
            <p className="mt-1 text-sm text-gray-600 dark:text-gray-400">
              管理您的文档库，上传新文档或删除不需要的文档
            </p>
          </div>
          <Button
            onClick={() => openModal('uploadDocument')}
            className="gap-2"
          >
            <Upload className="w-4 h-4" />
            上传文档
          </Button>
        </div>
      </div>

      {/* 文档列表 */}
      <div className="flex-1 overflow-y-auto p-6">
        <div className="max-w-4xl mx-auto">
          <DocumentList
            documents={documents}
            loading={loading}
            onDelete={handleDelete}
            onRefresh={handleRefresh}
          />
        </div>
      </div>
    </div>
  );
};

export default DocumentsPage;
