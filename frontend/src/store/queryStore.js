import { create } from 'zustand';
import { devtools } from 'zustand/middleware';
import queryService from '../services/queryService';

const useQueryStore = create(
  devtools(
    (set, get) => ({
      // 状态
      messages: [], // 聊天消息列表
      currentQuery: '',
      isQuerying: false,
      error: null,
      queryMode: 'AUTO', // AUTO, NLP, RAG
      history: [],
      isLoadingHistory: false,

      // 设置查询内容
      setCurrentQuery: (query) => {
        set({ currentQuery: query });
      },

      // 设置查询模式
      setQueryMode: (mode) => {
        set({ queryMode: mode });
      },

      // 发送查询
      sendQuery: async (question) => {
        const { queryMode, messages } = get();
        
        // 添加用户消息
        const userMessage = {
          id: Date.now(),
          role: 'user',
          content: question,
          timestamp: new Date().toISOString()
        };
        
        set({ 
          messages: [...messages, userMessage],
          isQuerying: true,
          error: null,
          currentQuery: ''
        });

        try {
          const response = await queryService.query({
            question,
            mode: queryMode
          });

          // 添加助手回复
          const assistantMessage = {
            id: Date.now() + 1,
            role: 'assistant',
            content: response.answer,
            sources: response.sources || [],
            mode: response.mode,
            timestamp: new Date().toISOString()
          };

          set({ 
            messages: [...get().messages, assistantMessage],
            isQuerying: false
          });

          return response;
        } catch (error) {
          const errorMessage = {
            id: Date.now() + 1,
            role: 'assistant',
            content: '抱歉，查询过程中出现错误。请稍后重试。',
            error: true,
            timestamp: new Date().toISOString()
          };

          set({ 
            messages: [...get().messages, errorMessage],
            error: error.response?.data?.message || '查询失败',
            isQuerying: false
          });
          
          throw error;
        }
      },

      // 获取历史记录
      fetchHistory: async (limit = 50) => {
        set({ isLoadingHistory: true, error: null });
        try {
          const response = await queryService.getHistory(limit);
          set({ 
            history: response || [],
            isLoadingHistory: false
          });
        } catch (error) {
          set({ 
            error: error.response?.data?.message || '获取历史记录失败',
            isLoadingHistory: false
          });
        }
      },

      // 从历史记录加载对话
      loadHistoryItem: (historyItem) => {
        const userMessage = {
          id: Date.now(),
          role: 'user',
          content: historyItem.question,
          timestamp: historyItem.queryTime
        };

        const assistantMessage = {
          id: Date.now() + 1,
          role: 'assistant',
          content: historyItem.answer,
          sources: historyItem.sources || [],
          mode: historyItem.mode,
          timestamp: historyItem.queryTime
        };

        set({ 
          messages: [userMessage, assistantMessage]
        });
      },

      // 清除消息
      clearMessages: () => {
        set({ messages: [] });
      },

      // 清除错误
      clearError: () => {
        set({ error: null });
      },

      // 重置状态
      reset: () => {
        set({
          messages: [],
          currentQuery: '',
          isQuerying: false,
          error: null,
          queryMode: 'AUTO',
          history: [],
          isLoadingHistory: false
        });
      }
    }),
    { name: 'QueryStore' }
  )
);

export default useQueryStore;
