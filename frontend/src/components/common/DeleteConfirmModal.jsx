import React, { useState } from 'react';
import { ConfirmModal } from './Modal';
import useUIStore from '../../store/uiStore';
import useDocumentStore from '../../store/documentStore';

/**
 * 删除确认模态框组件
 * 用于确认删除文档操作
 */
const DeleteConfirmModal = () => {
  const { modals, closeModal, addNotification } = useUIStore();
  const { deleteDocument } = useDocumentStore();
  const [loading, setLoading] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState(null);

  const handleConfirm = async () => {
    if (!deleteTarget) return;

    setLoading(true);
    try {
      await deleteDocument(deleteTarget.id);
      addNotification({
        type: 'success',
        message: `文档 "${deleteTarget.name}" 已成功删除`
      });
      closeModal('deleteConfirm');
      setDeleteTarget(null);
    } catch (error) {
      addNotification({
        type: 'error',
        message: error.message || '删除文档失败'
      });
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    if (!loading) {
      closeModal('deleteConfirm');
      setDeleteTarget(null);
    }
  };

  return (
    <ConfirmModal
      isOpen={modals.deleteConfirm}
      onClose={handleClose}
      onConfirm={handleConfirm}
      title="确认删除"
      message={
        deleteTarget
          ? `确定要删除文档 "${deleteTarget.name}" 吗？此操作无法撤销。`
          : '确定要删除此文档吗？此操作无法撤销。'
      }
      confirmText="删除"
      cancelText="取消"
      variant="danger"
      loading={loading}
    />
  );
};

// 导出一个辅助函数用于打开删除确认模态框
export const openDeleteConfirm = (document, uiStore) => {
  // 这个函数可以在其他组件中使用
  // 但由于状态管理的限制，我们需要通过事件或其他方式传递文档信息
  uiStore.openModal('deleteConfirm');
};

export default DeleteConfirmModal;
