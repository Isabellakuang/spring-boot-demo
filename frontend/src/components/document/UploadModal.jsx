import React, { useState, useRef } from 'react';
import { Upload, X, FileText, AlertCircle, CheckCircle } from 'lucide-react';
import Modal from '../common/Modal';
import Button from '../common/Button';
import useUIStore from '../../store/uiStore';
import useDocumentStore from '../../store/documentStore';
import { validateFile } from '../../utils/validators';
import { formatFileSize } from '../../utils/formatters';

/**
 * 文档上传模态框组件
 */
const UploadModal = () => {
  const { modals, closeModal, addNotification } = useUIStore();
  const { uploadDocument } = useDocumentStore();
  const [selectedFile, setSelectedFile] = useState(null);
  const [error, setError] = useState('');
  const [uploading, setUploading] = useState(false);
  const fileInputRef = useRef(null);

  // 处理文件选择
  const handleFileSelect = (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const validation = validateFile(file);
    if (!validation.valid) {
      setError(validation.error);
      setSelectedFile(null);
      return;
    }

    setError('');
    setSelectedFile(file);
  };

  // 处理拖放
  const handleDrop = (e) => {
    e.preventDefault();
    const file = e.dataTransfer.files?.[0];
    if (!file) return;

    const validation = validateFile(file);
    if (!validation.valid) {
      setError(validation.error);
      setSelectedFile(null);
      return;
    }

    setError('');
    setSelectedFile(file);
  };

  const handleDragOver = (e) => {
    e.preventDefault();
  };

  // 处理上传
  const handleUpload = async () => {
    if (!selectedFile) return;

    setUploading(true);
    try {
      await uploadDocument(selectedFile);
      addNotification({
        type: 'success',
        message: '文档上传成功'
      });
      handleClose();
    } catch (err) {
      setError(err.message || '上传失败，请重试');
      addNotification({
        type: 'error',
        message: err.message || '上传失败，请重试'
      });
    } finally {
      setUploading(false);
    }
  };

  // 关闭模态框
  const handleClose = () => {
    setSelectedFile(null);
    setError('');
    setUploading(false);
    closeModal('uploadDocument');
  };

  return (
    <Modal
      isOpen={modals.uploadDocument}
      onClose={handleClose}
      title="上传文档"
      size="lg"
    >
      <div className="space-y-4">
        {/* 拖放区域 */}
        <div
          onDrop={handleDrop}
          onDragOver={handleDragOver}
          onClick={() => fileInputRef.current?.click()}
          className={`
            relative border-2 border-dashed rounded-lg p-8
            cursor-pointer transition-all duration-200
            ${selectedFile 
              ? 'border-green-300 bg-green-50' 
              : 'border-gray-300 bg-gray-50 hover:border-primary-400 hover:bg-primary-50'
            }
          `}
        >
          <input
            ref={fileInputRef}
            type="file"
            accept=".pdf"
            onChange={handleFileSelect}
            className="hidden"
          />

          <div className="flex flex-col items-center gap-3">
            {selectedFile ? (
              <>
                <CheckCircle className="w-12 h-12 text-green-500" />
                <div className="text-center">
                  <p className="text-sm font-medium text-gray-900">
                    {selectedFile.name}
                  </p>
                  <p className="text-xs text-gray-500 mt-1">
                    {formatFileSize(selectedFile.size)}
                  </p>
                </div>
              </>
            ) : (
              <>
                <Upload className="w-12 h-12 text-gray-400" />
                <div className="text-center">
                  <p className="text-sm font-medium text-gray-900">
                    点击或拖放文件到此处
                  </p>
                  <p className="text-xs text-gray-500 mt-1">
                    支持PDF格式，最大10MB
                  </p>
                </div>
              </>
            )}
          </div>
        </div>

        {/* 错误提示 */}
        {error && (
          <div className="flex items-start gap-2 p-3 bg-red-50 border border-red-200 rounded-lg">
            <AlertCircle className="w-5 h-5 text-red-500 flex-shrink-0 mt-0.5" />
            <p className="text-sm text-red-700">{error}</p>
          </div>
        )}

        {/* 文件信息 */}
        {selectedFile && (
          <div className="flex items-center gap-3 p-3 bg-white border border-gray-200 rounded-lg">
            <FileText className="w-8 h-8 text-primary-600" />
            <div className="flex-1 min-w-0">
              <p className="text-sm font-medium text-gray-900 truncate">
                {selectedFile.name}
              </p>
              <p className="text-xs text-gray-500">
                {formatFileSize(selectedFile.size)}
              </p>
            </div>
            <button
              onClick={(e) => {
                e.stopPropagation();
                setSelectedFile(null);
                setError('');
              }}
              className="p-1 hover:bg-gray-100 rounded transition-colors"
            >
              <X className="w-4 h-4 text-gray-500" />
            </button>
          </div>
        )}

        {/* 操作按钮 */}
        <div className="flex justify-end gap-3 pt-4 border-t">
          <Button
            onClick={handleClose}
            variant="outline"
            disabled={uploading}
          >
            取消
          </Button>
          <Button
            onClick={handleUpload}
            variant="primary"
            disabled={!selectedFile || uploading}
            loading={uploading}
          >
            {uploading ? '上传中...' : '上传'}
          </Button>
        </div>
      </div>
    </Modal>
  );
};

export default UploadModal;
