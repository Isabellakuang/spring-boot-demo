import React from 'react';
import { Menu, Sun, Moon, Github } from 'lucide-react';
import useUIStore from '../../store/uiStore';

const Header = () => {
  const { theme, toggleTheme, toggleSidebar } = useUIStore();

  return (
    <header className="bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700 sticky top-0 z-40">
      <div className="flex items-center justify-between px-4 py-3">
        {/* 左侧：菜单按钮和标题 */}
        <div className="flex items-center space-x-4">
          <button
            onClick={toggleSidebar}
            className="p-2 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors lg:hidden"
            aria-label="Toggle sidebar"
          >
            <Menu className="w-5 h-5 text-gray-600 dark:text-gray-300" />
          </button>
          
          <div className="flex items-center space-x-3">
            <div className="w-8 h-8 bg-gradient-to-br from-blue-500 to-purple-600 rounded-lg flex items-center justify-center">
              <span className="text-white font-bold text-sm">SCB</span>
            </div>
            <div>
              <h1 className="text-lg font-bold text-gray-900 dark:text-white">
                RAG Demo
              </h1>
              <p className="text-xs text-gray-500 dark:text-gray-400">
                Intelligent Document Q&A
              </p>
            </div>
          </div>
        </div>

        {/* 右侧：主题切换和GitHub链接 */}
        <div className="flex items-center space-x-2">
          <button
            onClick={toggleTheme}
            className="p-2 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
            aria-label="Toggle theme"
          >
            {theme === 'light' ? (
              <Moon className="w-5 h-5 text-gray-600 dark:text-gray-300" />
            ) : (
              <Sun className="w-5 h-5 text-gray-600 dark:text-gray-300" />
            )}
          </button>

          <a
            href="https://github.com/Isabellakuang/scb-rag-demo"
            target="_blank"
            rel="noopener noreferrer"
            className="p-2 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
            aria-label="View on GitHub"
          >
            <Github className="w-5 h-5 text-gray-600 dark:text-gray-300" />
          </a>
        </div>
      </div>
    </header>
  );
};

export default Header;
