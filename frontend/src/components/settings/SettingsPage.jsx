import React from 'react';
import { Settings, Moon, Sun, Info } from 'lucide-react';
import Button from '../common/Button';
import useUIStore from '../../store/uiStore';

/**
 * 设置页面
 */
const SettingsPage = () => {
  const { theme, toggleTheme } = useUIStore();

  return (
    <div className="h-full flex flex-col bg-gray-50 dark:bg-gray-900">
      {/* 页面头部 */}
      <div className="bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700 px-6 py-4">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900 dark:text-white">
              设置
            </h1>
            <p className="mt-1 text-sm text-gray-600 dark:text-gray-400">
              配置您的应用偏好设置
            </p>
          </div>
        </div>
      </div>

      {/* 设置内容 */}
      <div className="flex-1 overflow-y-auto p-6">
        <div className="max-w-2xl mx-auto space-y-6">
          {/* 外观设置 */}
          <div className="bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700 p-6">
            <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
              外观
            </h2>
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                {theme === 'dark' ? (
                  <Moon className="w-5 h-5 text-gray-600 dark:text-gray-400" />
                ) : (
                  <Sun className="w-5 h-5 text-gray-600 dark:text-gray-400" />
                )}
                <div>
                  <p className="text-sm font-medium text-gray-900 dark:text-white">
                    主题模式
                  </p>
                  <p className="text-xs text-gray-600 dark:text-gray-400">
                    当前: {theme === 'dark' ? '深色' : '浅色'}
                  </p>
                </div>
              </div>
              <Button onClick={toggleTheme} variant="outline" size="sm">
                切换主题
              </Button>
            </div>
          </div>

          {/* 关于信息 */}
          <div className="bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700 p-6">
            <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
              关于
            </h2>
            <div className="space-y-3">
              <div className="flex items-start gap-3">
                <Info className="w-5 h-5 text-gray-600 dark:text-gray-400 mt-0.5" />
                <div>
                  <p className="text-sm font-medium text-gray-900 dark:text-white">
                    SCB RAG Demo
                  </p>
                  <p className="text-xs text-gray-600 dark:text-gray-400 mt-1">
                    版本: 1.0.0
                  </p>
                  <p className="text-xs text-gray-600 dark:text-gray-400 mt-2">
                    基于检索增强生成（RAG）技术的智能文档问答系统
                  </p>
                </div>
              </div>
            </div>
          </div>

          {/* 系统信息 */}
          <div className="bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700 p-6">
            <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
              系统信息
            </h2>
            <div className="space-y-2 text-sm">
              <div className="flex justify-between">
                <span className="text-gray-600 dark:text-gray-400">后端API:</span>
                <span className="font-medium text-gray-900 dark:text-white">
                  {import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600 dark:text-gray-400">环境:</span>
                <span className="font-medium text-gray-900 dark:text-white">
                  {import.meta.env.MODE}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SettingsPage;
