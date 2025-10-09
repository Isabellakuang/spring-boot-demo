import { create } from 'zustand';
import { devtools, persist } from 'zustand/middleware';

const useUIStore = create(
  devtools(
    persist(
      (set) => ({
        // 侧边栏状态
        sidebarOpen: true,
        sidebarTab: 'documents', // 'documents' | 'history'
        activeTab: 'chat', // 'chat' | 'documents' | 'history' | 'settings'
        
        // 主题
        theme: 'light', // 'light' | 'dark'
        
        // 通知
        notifications: [],
        
        // 模态框
        modals: {
          uploadDocument: false,
          deleteConfirm: false,
          settings: false
        },
        
        // 加载状态
        globalLoading: false,

        // 切换侧边栏
        toggleSidebar: () => {
          set((state) => ({ sidebarOpen: !state.sidebarOpen }));
        },

        // 设置侧边栏状态
        setSidebarOpen: (open) => {
          set({ sidebarOpen: open });
        },

        // 切换侧边栏标签
        setSidebarTab: (tab) => {
          set({ sidebarTab: tab });
        },

        // 设置活动标签
        setActiveTab: (tab) => {
          set({ activeTab: tab });
        },

        // 切换主题
        toggleTheme: () => {
          set((state) => ({ 
            theme: state.theme === 'light' ? 'dark' : 'light' 
          }));
        },

        // 设置主题
        setTheme: (theme) => {
          set({ theme });
        },

        // 添加通知
        addNotification: (notification) => {
          const id = Date.now();
          const newNotification = {
            id,
            type: 'info', // 'success' | 'error' | 'warning' | 'info'
            duration: 3000,
            ...notification
          };

          set((state) => ({
            notifications: [...state.notifications, newNotification]
          }));

          // 自动移除通知
          if (newNotification.duration > 0) {
            setTimeout(() => {
              set((state) => ({
                notifications: state.notifications.filter(n => n.id !== id)
              }));
            }, newNotification.duration);
          }

          return id;
        },

        // 移除通知
        removeNotification: (id) => {
          set((state) => ({
            notifications: state.notifications.filter(n => n.id !== id)
          }));
        },

        // 清除所有通知
        clearNotifications: () => {
          set({ notifications: [] });
        },

        // 打开模态框
        openModal: (modalName) => {
          set((state) => ({
            modals: { ...state.modals, [modalName]: true }
          }));
        },

        // 关闭模态框
        closeModal: (modalName) => {
          set((state) => ({
            modals: { ...state.modals, [modalName]: false }
          }));
        },

        // 关闭所有模态框
        closeAllModals: () => {
          set((state) => ({
            modals: Object.keys(state.modals).reduce((acc, key) => {
              acc[key] = false;
              return acc;
            }, {})
          }));
        },

        // 设置全局加载状态
        setGlobalLoading: (loading) => {
          set({ globalLoading: loading });
        },

        // 重置UI状态
        reset: () => {
          set({
            sidebarOpen: true,
            sidebarTab: 'documents',
            activeTab: 'chat',
            notifications: [],
            modals: {
              uploadDocument: false,
              deleteConfirm: false,
              settings: false
            },
            globalLoading: false
          });
        }
      }),
      {
        name: 'ui-storage',
        partialize: (state) => ({
          theme: state.theme,
          sidebarOpen: state.sidebarOpen
        })
      }
    ),
    { name: 'UIStore' }
  )
);

export default useUIStore;
