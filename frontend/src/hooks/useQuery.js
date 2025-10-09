import { useEffect } from 'react';
import useQueryStore from '../store/queryStore';
import useUIStore from '../store/uiStore';

/**
 * 查询管理自定义Hook
 * 提供查询相关的状态和操作方法
 */
export const useQuery = () => {
  const {
    messages,
    currentQuery,
    queryMode,
    isLoading,
    history,
    sendQuery,
    setCurrentQuery,
    setQueryMode,
    clearMessages,
    fetchHistory,
    loadHistoryItem,
  } = useQueryStore();

  const { addNotification } = useUIStore();

  // 组件挂载时获取历史记录
  useEffect(() => {
    fetchHistory(20);
  }, [fetchHistory]);

  /**
   * 处理查询提交
   */
  const handleSubmit = async (question) => {
    if (!question?.trim()) {
      addNotification({
        type: 'warning',
        message: '请输入查询内容',
      });
      return;
    }

    try {
      await sendQuery(question.trim());
      setCurrentQuery('');
    } catch (error) {
      addNotification({
        type: 'error',
        message: error.message || '查询失败，请重试',
      });
    }
  };

  /**
   * 处理模式切换
   */
  const handleModeChange = (mode) => {
    setQueryMode(mode);
    addNotification({
      type: 'info',
      message: `已切换到 ${mode} 模式`,
    });
  };

  /**
   * 处理历史记录加载
   */
  const handleLoadHistory = (historyItem) => {
    loadHistoryItem(historyItem);
    addNotification({
      type: 'success',
      message: '已加载历史对话',
    });
  };

  /**
   * 处理清空对话
   */
  const handleClearMessages = () => {
    clearMessages();
    addNotification({
      type: 'success',
      message: '对话已清空',
    });
  };

  /**
   * 刷新历史记录
   */
  const refreshHistory = async () => {
    try {
      await fetchHistory(20);
      addNotification({
        type: 'success',
        message: '历史记录已刷新',
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
    messages,
    currentQuery,
    queryMode,
    isLoading,
    history,
    // 操作方法
    handleSubmit,
    handleModeChange,
    handleLoadHistory,
    handleClearMessages,
    setCurrentQuery,
    refreshHistory,
  };
};
