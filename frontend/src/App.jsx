import React, { useEffect } from 'react';
import useUIStore from './store/uiStore';
import Header from './components/layout/Header';
import Sidebar from './components/layout/Sidebar';
import ChatPanel from './components/chat/ChatPanel';
import DocumentsPage from './components/document/DocumentsPage';
import HistoryPage from './components/history/HistoryPage';
import SettingsPage from './components/settings/SettingsPage';
import NotificationContainer from './components/common/NotificationContainer';
import UploadModal from './components/document/UploadModal';
import DeleteConfirmModal from './components/common/DeleteConfirmModal';

/**
 * 应用根组件
 * 包含主要布局和全局组件
 */
function App() {
  const { activeTab, theme } = useUIStore();

  // 应用主题到document元素
  useEffect(() => {
    if (theme === 'dark') {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }, [theme]);

  // 根据activeTab渲染不同的内容
  const renderContent = () => {
    switch (activeTab) {
      case 'chat':
        return <ChatPanel />;
      case 'documents':
        return <DocumentsPage />;
      case 'history':
        return <HistoryPage />;
      case 'settings':
        return <SettingsPage />;
      default:
        return <ChatPanel />;
    }
  };

  return (
    <div className="app">
      <div className="min-h-screen bg-gray-50 dark:bg-gray-900 transition-colors">
        {/* 顶部导航栏 */}
        <Header />

        {/* 主内容区域 */}
        <div className="flex h-[calc(100vh-4rem)]">
          {/* 侧边栏 */}
          <Sidebar />

          {/* 主内容 */}
          <main className="flex-1 overflow-hidden">
            {renderContent()}
          </main>
        </div>

        {/* 全局通知容器 */}
        <NotificationContainer />

        {/* 模态框 */}
        <UploadModal />
        <DeleteConfirmModal />
      </div>
    </div>
  );
}

export default App;
