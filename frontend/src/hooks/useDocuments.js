import { useEffect } from 'react';
import { useDocumentStore } from '../store/documentStore';
import { useUIStore } from '../store/uiStore';

/**
 * 文档管理自定义Hook
 * 提供文档相关的状态和操作方法
 */
export const useDocuments = () => {
  const {
    documents,
    selectedDocument,
    uploadProgress,
    isUploading,
    stats,
    fetchDocuments,
    uploadDocument,
    deleteDocument,
    fetchStats,
    selectDocument,
  } = useDocumentStore();

  const { addNotification, openModal, closeModal } = useUIStore();

  // 组件挂载时获取文档列表和统计信息
  useEffect(() => {
    fetchDocuments();
    fetchStats();
  }, [fetchDocuments, fetchStats]);

  /**
   * 处理文件上传
   */
  const handleUpload = async (file) => {
    try {
      await uploadDocument(file);
      addNotification({
        type: 'success',
        message: `文档 "${file.name}" 上传成功`,
      });
      closeModal('uploadDocument');
      // 刷新文档列表和统计信息
      await fetchDocuments();
      await fetchStats();
    } catch (error) {
      addNotification({
        type: 'error',
        message: error.message || '文档上传失败',
      });
    }
  };

  /**
   * 处理文档删除
   */
  const handleDelete = async (documentId) => {
    try {
      await deleteDocument(documentId);
      addNotification({
        type: 'success',
        message: '文档删除成功',
      });
      closeModal('deleteConfirm');
      // 刷新文档列表和统计信息
      await fetchDocuments();
      await fetchStats();
      // 如果删除的是当前选中的文档，清除选中状态
      if (selectedDocument?.id === documentId) {
        selectDocument(null);
      }
    } catch (error) {
      addNotification({
        type: 'error',
        message: error.message || '文档删除失败',
      });
    }
  };

  /**
   * 打开上传对话框
   */
  const openUploadDialog = () => {
    openModal('uploadDocument');
  };

  /**
   * 打开删除确认对话框
   */
  const openDeleteDialog = (document) => {
    selectDocument(document);
    openModal('deleteConfirm');
  };

  /**
   * 刷新文档列表
   */
  const refresh = async () => {
    try {
      await fetchDocuments();
      await fetchStats();
      addNotification({
        type: 'success',
        message: '文档列表已刷新',
      });
    } catch (error) {
      addNotification({
        type: 'error',
        message: '刷新失败',
      });
    }
  };

  return {
    // 状态
    documents,
    selectedDocument,
    uploadProgress,
    isUploading,
    stats,
    // 操作方法
    handleUpload,
    handleDelete,
    selectDocument,
    openUploadDialog,
    openDeleteDialog,
    refresh,
  };
};
