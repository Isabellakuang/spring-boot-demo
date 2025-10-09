import api from './api';

/**
 * 文档服务 - 处理文档相关的API调用
 */
const documentService = {
  /**
   * 上传文档
   * @param {File} file - PDF文件
   * @returns {Promise} 上传结果
   */
  uploadDocument: async (file) => {
    const formData = new FormData();
    formData.append('file', file);

    return api.post('/documents/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  },

  /**
   * 获取所有文档列表
   * @returns {Promise} 文档列表
   */
  getAllDocuments: async () => {
    return api.get('/documents');
  },

  /**
   * 根据ID获取文档详情
   * @param {number} id - 文档ID
   * @returns {Promise} 文档详情
   */
  getDocumentById: async (id) => {
    return api.get(`/documents/${id}`);
  },

  /**
   * 删除文档
   * @param {number} id - 文档ID
   * @returns {Promise} 删除结果
   */
  deleteDocument: async (id) => {
    return api.delete(`/documents/${id}`);
  },

  /**
   * 获取文档统计信息
   * @returns {Promise} 统计信息
   */
  getDocumentStats: async () => {
    return api.get('/documents/stats');
  },
};

export default documentService;
