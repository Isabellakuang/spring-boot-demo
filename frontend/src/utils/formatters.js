/**
 * 格式化文件大小
 * @param {number} bytes - 字节数
 * @returns {string} 格式化后的文件大小
 */
export const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 Bytes';
  
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
};

/**
 * 格式化日期时间
 * @param {string|Date} date - 日期
 * @param {boolean} includeTime - 是否包含时间
 * @returns {string} 格式化后的日期时间
 */
export const formatDateTime = (date, includeTime = true) => {
  if (!date) return '';
  
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  
  if (!includeTime) {
    return `${year}-${month}-${day}`;
  }
  
  const hours = String(d.getHours()).padStart(2, '0');
  const minutes = String(d.getMinutes()).padStart(2, '0');
  const seconds = String(d.getSeconds()).padStart(2, '0');
  
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};

/**
 * 格式化相对时间
 * @param {string|Date} date - 日期
 * @returns {string} 相对时间描述
 */
export const formatRelativeTime = (date) => {
  if (!date) return '';
  
  const now = new Date();
  const past = new Date(date);
  const diffMs = now - past;
  const diffSec = Math.floor(diffMs / 1000);
  const diffMin = Math.floor(diffSec / 60);
  const diffHour = Math.floor(diffMin / 60);
  const diffDay = Math.floor(diffHour / 24);
  
  if (diffSec < 60) return '刚刚';
  if (diffMin < 60) return `${diffMin}分钟前`;
  if (diffHour < 24) return `${diffHour}小时前`;
  if (diffDay < 7) return `${diffDay}天前`;
  
  return formatDateTime(date, false);
};

/**
 * 截断文本
 * @param {string} text - 文本
 * @param {number} maxLength - 最大长度
 * @returns {string} 截断后的文本
 */
export const truncateText = (text, maxLength = 100) => {
  if (!text || text.length <= maxLength) return text;
  return text.substring(0, maxLength) + '...';
};

/**
 * 高亮搜索关键词
 * @param {string} text - 文本
 * @param {string} keyword - 关键词
 * @returns {string} 带高亮标记的HTML
 */
export const highlightKeyword = (text, keyword) => {
  if (!text || !keyword) return text;
  
  const regex = new RegExp(`(${keyword})`, 'gi');
  return text.replace(regex, '<mark>$1</mark>');
};

/**
 * 高亮文本中的查询词
 * @param {string} text - 文本
 * @param {string} query - 查询词
 * @returns {string} 带高亮标记的HTML
 */
export const highlightText = (text, query) => {
  if (!text || !query) return text;
  
  // 转义特殊字符
  const escapedQuery = query.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
  
  // 分词并高亮每个词
  const words = escapedQuery.split(/\s+/).filter(word => word.length > 0);
  let result = text;
  
  words.forEach(word => {
    const regex = new RegExp(`(${word})`, 'gi');
    result = result.replace(regex, '<mark class="bg-yellow-200">$1</mark>');
  });
  
  return result;
};

/**
 * 格式化查询模式
 * @param {string} mode - 查询模式
 * @returns {object} 模式信息
 */
export const formatQueryMode = (mode) => {
  const modes = {
    AUTO: { label: '自动', color: 'blue', icon: '🤖' },
    NLP: { label: 'NLP', color: 'green', icon: '💬' },
    RAG: { label: 'RAG', color: 'purple', icon: '📚' }
  };
  
  return modes[mode] || modes.AUTO;
};

/**
 * 格式化文档状态
 * @param {string} status - 文档状态
 * @returns {object} 状态信息
 */
export const formatDocumentStatus = (status) => {
  const statuses = {
    UPLOADED: { label: '已上传', color: 'blue', icon: '📤' },
    PROCESSING: { label: '处理中', color: 'yellow', icon: '⚙️' },
    READY: { label: '就绪', color: 'green', icon: '✅' },
    FAILED: { label: '失败', color: 'red', icon: '❌' }
  };
  
  return statuses[status] || statuses.UPLOADED;
};

/**
 * 生成随机ID
 * @returns {string} 随机ID
 */
export const generateId = () => {
  return Date.now().toString(36) + Math.random().toString(36).substr(2);
};

/**
 * 防抖函数
 * @param {Function} func - 要防抖的函数
 * @param {number} wait - 等待时间（毫秒）
 * @returns {Function} 防抖后的函数
 */
export const debounce = (func, wait = 300) => {
  let timeout;
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
};

/**
 * 节流函数
 * @param {Function} func - 要节流的函数
 * @param {number} limit - 时间限制（毫秒）
 * @returns {Function} 节流后的函数
 */
export const throttle = (func, limit = 300) => {
  let inThrottle;
  return function executedFunction(...args) {
    if (!inThrottle) {
      func(...args);
      inThrottle = true;
      setTimeout(() => (inThrottle = false), limit);
    }
  };
};
