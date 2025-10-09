import api from './api';

/**
 * 查询服务 - 处理查询相关的API调用
 */
const queryService = {
  /**
   * 执行查询
   * @param {Object} queryData - 查询数据
   * @param {string} queryData.question - 查询问题
   * @param {string} queryData.mode - 查询模式 (AUTO/NLP/RAG)
   * @returns {Promise} 查询结果
   */
  query: async (queryData) => {
    return api.post('/query', queryData);
  },

  // 别名方法，保持向后兼容
  executeQuery: async (queryData) => {
    return queryService.query(queryData);
  },

  /**
   * 获取查询历史
   * @param {number} limit - 返回记录数量限制
   * @returns {Promise} 查询历史列表
   */
  getHistory: async (limit = 20) => {
    return api.get('/history', {
      params: { size: limit },
    });
  },

  /**
   * 获取查询历史（分页）
   * @param {number} page - 页码
   * @param {number} size - 每页大小
   * @returns {Promise} 查询历史列表
   */
  getQueryHistory: async (page = 0, size = 20) => {
    return api.get('/history', {
      params: { page, size },
    });
  },

  /**
   * 根据ID获取查询历史详情
   * @param {number} id - 查询历史ID
   * @returns {Promise} 查询历史详情
   */
  getQueryHistoryById: async (id) => {
    return api.get(`/history/${id}`);
  },

  /**
   * 删除查询历史
   * @param {number} id - 查询历史ID
   * @returns {Promise} 删除结果
   */
  deleteQueryHistory: async (id) => {
    return api.delete(`/history/${id}`);
  },

  /**
   * 清空所有查询历史
   * @returns {Promise} 清空结果
   */
  clearAllHistory: async () => {
    return api.delete('/history/all');
  },
};

export default queryService;
