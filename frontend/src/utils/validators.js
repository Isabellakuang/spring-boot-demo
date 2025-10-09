/**
 * 验证文件类型
 * @param {File} file - 文件对象
 * @param {string[]} allowedTypes - 允许的文件类型
 * @returns {boolean} 是否有效
 */
export const validateFileType = (file, allowedTypes = ['application/pdf']) => {
  if (!file) return false;
  return allowedTypes.includes(file.type);
};

/**
 * 验证文件大小
 * @param {File} file - 文件对象
 * @param {number} maxSize - 最大大小（字节）
 * @returns {boolean} 是否有效
 */
export const validateFileSize = (file, maxSize = 10 * 1024 * 1024) => {
  if (!file) return false;
  return file.size <= maxSize;
};

/**
 * 验证文件
 * @param {File} file - 文件对象
 * @returns {object} 验证结果
 */
export const validateFile = (file) => {
  const errors = [];
  
  if (!file) {
    errors.push('请选择文件');
    return { valid: false, errors };
  }
  
  // 验证文件类型
  if (!validateFileType(file)) {
    errors.push('只支持PDF文件');
  }
  
  // 验证文件大小（10MB）
  if (!validateFileSize(file, 10 * 1024 * 1024)) {
    errors.push('文件大小不能超过10MB');
  }
  
  // 验证文件名
  if (file.name.length > 255) {
    errors.push('文件名过长');
  }
  
  return {
    valid: errors.length === 0,
    errors
  };
};

/**
 * 验证查询文本
 * @param {string} text - 查询文本
 * @returns {object} 验证结果
 */
export const validateQuery = (text) => {
  const errors = [];
  
  if (!text || text.trim().length === 0) {
    errors.push('请输入查询内容');
    return { valid: false, errors };
  }
  
  if (text.trim().length < 2) {
    errors.push('查询内容至少需要2个字符');
  }
  
  if (text.length > 1000) {
    errors.push('查询内容不能超过1000个字符');
  }
  
  return {
    valid: errors.length === 0,
    errors
  };
};

/**
 * 验证邮箱
 * @param {string} email - 邮箱地址
 * @returns {boolean} 是否有效
 */
export const validateEmail = (email) => {
  const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return regex.test(email);
};

/**
 * 验证URL
 * @param {string} url - URL地址
 * @returns {boolean} 是否有效
 */
export const validateUrl = (url) => {
  try {
    new URL(url);
    return true;
  } catch {
    return false;
  }
};

/**
 * 清理文本
 * @param {string} text - 文本
 * @returns {string} 清理后的文本
 */
export const sanitizeText = (text) => {
  if (!text) return '';
  return text.trim().replace(/\s+/g, ' ');
};

/**
 * 验证查询模式
 * @param {string} mode - 查询模式
 * @returns {boolean} 是否有效
 */
export const validateQueryMode = (mode) => {
  const validModes = ['AUTO', 'NLP', 'RAG'];
  return validModes.includes(mode);
};
