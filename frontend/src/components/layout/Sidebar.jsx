import React from 'react';
import { FileText, MessageSquare, History, Settings, X } from 'lucide-react';
import useUIStore from '../../store/uiStore';
import useDocumentStore from '../../store/documentStore';

const Sidebar = () => {
  const { sidebarOpen, toggleSidebar, activeTab, setActiveTab } = useUIStore();
  const { documents, statistics } = useDocumentStore();

  const tabs = [
    {
      id: 'chat',
      name: 'Chat',
      icon: MessageSquare,
      badge: null,
    },
    {
      id: 'documents',
      name: 'Documents',
      icon: FileText,
      badge: documents.length,
    },
    {
      id: 'history',
      name: 'History',
      icon: History,
      badge: null,
    },
    {
      id: 'settings',
      name: 'Settings',
      icon: Settings,
      badge: null,
    },
  ];

  const handleTabClick = (tabId) => {
    setActiveTab(tabId);
    // 在移动设备上点击后关闭侧边栏
    if (window.innerWidth < 1024) {
      toggleSidebar();
    }
  };

  return (
    <>
      {/* 移动端遮罩层 */}
      {sidebarOpen && (
        <div
          className="fixed inset-0 bg-black bg-opacity-50 z-40 lg:hidden"
          onClick={toggleSidebar}
        />
      )}

      {/* 侧边栏 */}
      <aside
        className={`
          fixed lg:static inset-y-0 left-0 z-50
          w-64 bg-white dark:bg-gray-800 border-r border-gray-200 dark:border-gray-700
          transform transition-transform duration-300 ease-in-out
          ${sidebarOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'}
        `}
      >
        {/* 移动端关闭按钮 */}
        <div className="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700 lg:hidden">
          <h2 className="text-lg font-semibold text-gray-900 dark:text-white">Menu</h2>
          <button
            onClick={toggleSidebar}
            className="p-2 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700"
          >
            <X className="w-5 h-5 text-gray-600 dark:text-gray-300" />
          </button>
        </div>

        {/* 导航标签 */}
        <nav className="p-4 space-y-2">
          {tabs.map((tab) => {
            const Icon = tab.icon;
            const isActive = activeTab === tab.id;

            return (
              <button
                key={tab.id}
                onClick={() => handleTabClick(tab.id)}
                className={`
                  w-full flex items-center justify-between px-4 py-3 rounded-lg
                  transition-colors duration-200
                  ${
                    isActive
                      ? 'bg-blue-50 dark:bg-blue-900/20 text-blue-600 dark:text-blue-400'
                      : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700'
                  }
                `}
              >
                <div className="flex items-center space-x-3">
                  <Icon className="w-5 h-5" />
                  <span className="font-medium">{tab.name}</span>
                </div>
                {tab.badge !== null && tab.badge > 0 && (
                  <span
                    className={`
                      px-2 py-1 text-xs font-semibold rounded-full
                      ${
                        isActive
                          ? 'bg-blue-600 text-white'
                          : 'bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300'
                      }
                    `}
                  >
                    {tab.badge}
                  </span>
                )}
              </button>
            );
          })}
        </nav>

        {/* 统计信息 */}
        {statistics && (
          <div className="absolute bottom-0 left-0 right-0 p-4 border-t border-gray-200 dark:border-gray-700">
            <div className="space-y-2 text-sm">
              <div className="flex justify-between text-gray-600 dark:text-gray-400">
                <span>Total Documents:</span>
                <span className="font-semibold">{statistics.totalDocuments || 0}</span>
              </div>
              <div className="flex justify-between text-gray-600 dark:text-gray-400">
                <span>Total Chunks:</span>
                <span className="font-semibold">{statistics.totalChunks || 0}</span>
              </div>
              <div className="flex justify-between text-gray-600 dark:text-gray-400">
                <span>Processed:</span>
                <span className="font-semibold text-green-600 dark:text-green-400">
                  {statistics.processedDocuments || 0}
                </span>
              </div>
            </div>
          </div>
        )}
      </aside>
    </>
  );
};

export default Sidebar;
