import React from 'react';
import { Zap, Brain, Database } from 'lucide-react';
import useQueryStore from '../../store/queryStore';

/**
 * 查询模式选择器组件
 * 允许用户选择不同的查询模式
 */
const ModeSelector = () => {
  const { queryMode, setQueryMode } = useQueryStore();

  const modes = [
    {
      id: 'AUTO',
      name: 'Auto',
      description: '自动选择最佳模式',
      icon: Zap,
      color: 'text-purple-600 dark:text-purple-400'
    },
    {
      id: 'NLP',
      name: 'NLP',
      description: '自然语言处理',
      icon: Brain,
      color: 'text-blue-600 dark:text-blue-400'
    },
    {
      id: 'RAG',
      name: 'RAG',
      description: '检索增强生成',
      icon: Database,
      color: 'text-green-600 dark:text-green-400'
    }
  ];

  return (
    <div className="px-6 py-4">
      <div className="flex items-center space-x-2">
        <span className="text-sm font-medium text-gray-700 dark:text-gray-300">
          Query Mode:
        </span>
        <div className="flex space-x-2">
          {modes.map((mode) => {
            const Icon = mode.icon;
            const isActive = queryMode === mode.id;
            
            return (
              <button
                key={mode.id}
                onClick={() => setQueryMode(mode.id)}
                className={`
                  flex items-center space-x-2 px-4 py-2 rounded-lg
                  transition-all duration-200
                  ${
                    isActive
                      ? 'bg-primary-100 dark:bg-primary-900/30 border-2 border-primary-500'
                      : 'bg-gray-100 dark:bg-gray-700 border-2 border-transparent hover:border-gray-300 dark:hover:border-gray-600'
                  }
                `}
                title={mode.description}
              >
                <Icon
                  className={`w-4 h-4 ${
                    isActive ? mode.color : 'text-gray-500 dark:text-gray-400'
                  }`}
                />
                <span
                  className={`text-sm font-medium ${
                    isActive
                      ? 'text-primary-700 dark:text-primary-300'
                      : 'text-gray-700 dark:text-gray-300'
                  }`}
                >
                  {mode.name}
                </span>
              </button>
            );
          })}
        </div>
      </div>
    </div>
  );
};

export default ModeSelector;
