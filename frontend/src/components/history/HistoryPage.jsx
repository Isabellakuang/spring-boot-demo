import React, { useEffect, useState } from 'react';
import { History, RefreshCw, Trash2 } from 'lucide-react';
import Button from '../common/Button';
import EmptyState from '../common/EmptyState';
import Loading from '../common/Loading';
import queryService from '../../services/queryService';
import useUIStore from '../../store/uiStore';

/**
 * 查询历史页面
 */
const HistoryPage = () => {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(false);
  const { addNotification } = useUIStore();

  const fetchHistory = async () => {
    setLoading(true);
    try {
      const data = await queryService.getHistory();
      // 后端返回的是分页数据，需要提取content数组
      setHistory(data?.content || []);
    } catch (error) {
      addNotification({
        type: 'error',
        message: '获取历史记录失败',
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchHistory();
  }, []);

  const handleClearHistory = async () => {
    if (!window.confirm('确定要清空所有历史记录吗？')) {
      return;
    }
    
    try {
      // 这里需要后端提供清空历史的API
      addNotification({
        type: 'success',
        message: '历史记录已清空',
      });
      setHistory([]);
    } catch (error) {
      addNotification({
        type: 'error',
        message: '清空历史记录失败',
      });
    }
  };

  if (loading) {
    return (
      <div className="h-full flex items-center justify-center">
        <Loading text="加载历史记录..." />
      </div>
    );
  }

  return (
    <div className="h-full flex flex-col bg-gray-50 dark:bg-gray-900">
      {/* 页面头部 */}
      <div className="bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700 px-6 py-4">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900 dark:text-white">
              查询历史
            </h1>
            <p className="mt-1 text-sm text-gray-600 dark:text-gray-400">
              查看您的历史查询记录
            </p>
          </div>
          <div className="flex gap-2">
            <Button
              onClick={fetchHistory}
              variant="ghost"
              size="sm"
              className="gap-2"
            >
              <RefreshCw className="w-4 h-4" />
              刷新
            </Button>
            {history.length > 0 && (
              <Button
                onClick={handleClearHistory}
                variant="ghost"
                size="sm"
                className="gap-2 text-red-600 hover:text-red-700"
              >
                <Trash2 className="w-4 h-4" />
                清空
              </Button>
            )}
          </div>
        </div>
      </div>

      {/* 历史记录列表 */}
      <div className="flex-1 overflow-y-auto p-6">
        <div className="max-w-4xl mx-auto">
          {history.length === 0 ? (
            <EmptyState
              icon={History}
              title="暂无历史记录"
              description="开始提问后，您的查询历史将显示在这里"
            />
          ) : (
            <div className="space-y-4">
              {history.map((item) => (
                <div
                  key={item.id}
                  className="bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700 p-4"
                >
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <div className="flex items-center gap-2 mb-2">
                        <span className="text-xs font-medium px-2 py-1 rounded bg-blue-100 dark:bg-blue-900 text-blue-700 dark:text-blue-300">
                          {item.queryType}
                        </span>
                        <span className="text-xs text-gray-500 dark:text-gray-400">
                          {new Date(item.createdAt).toLocaleString('zh-CN')}
                        </span>
                      </div>
                      <p className="text-sm font-medium text-gray-900 dark:text-white mb-2">
                        {item.queryText}
                      </p>
                      <p className="text-sm text-gray-600 dark:text-gray-400 line-clamp-2">
                        {item.responseText}
                      </p>
                      {item.responseTimeMs && (
                        <div className="mt-2 text-xs text-gray-500 dark:text-gray-400">
                          响应时间: {item.responseTimeMs}ms
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default HistoryPage;
