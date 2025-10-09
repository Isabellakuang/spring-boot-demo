import { create } from 'zustand';
import { devtools } from 'zustand/middleware';
import documentService from '../services/documentService';

const useDocumentStore = create(
  devtools(
    (set, get) => ({
      // 状态
      documents: [],
      selectedDocument: null,
      uploadProgress: 0,
      isUploading: false,
      isLoading: false,
      error: null,
      stats: {
        totalDocuments: 0,
        totalChunks: 0,
        processingDocuments: 0
      },

      // 获取文档列表
      fetchDocuments: async () => {
        set({ isLoading: true, error: null });
        try {
          const response = await documentService.getAllDocuments();
          set({ 
            documents: response.content || response || [],
            isLoading: false 
          });
        } catch (error) {
          set({ 
            error: error.message || '获取文档列表失败',
            isLoading: false 
          });
        }
      },

      // 上传文档
      uploadDocument: async (file) => {
        set({ isUploading: true, uploadProgress: 0, error: null });
        try {
          const response = await documentService.uploadDocument(
            file,
            (progressEvent) => {
              const progress = Math.round(
                (progressEvent.loaded * 100) / progressEvent.total
              );
              set({ uploadProgress: progress });
            }
          );
          
          // 上传成功后刷新文档列表
          await get().fetchDocuments();
          await get().fetchStats();
          
          set({ 
            isUploading: false,
            uploadProgress: 0 
          });
          
          return response.data;
        } catch (error) {
          set({ 
            error: error.response?.data?.message || '文档上传失败',
            isUploading: false,
            uploadProgress: 0
          });
          throw error;
        }
      },

      // 删除文档
      deleteDocument: async (documentId) => {
        set({ error: null });
        try {
          await documentService.deleteDocument(documentId);
          
          // 删除成功后刷新文档列表
          await get().fetchDocuments();
          await get().fetchStats();
          
          // 如果删除的是当前选中的文档，清除选中状态
          if (get().selectedDocument?.id === documentId) {
            set({ selectedDocument: null });
          }
        } catch (error) {
          set({ 
            error: error.response?.data?.message || '删除文档失败'
          });
          throw error;
        }
      },

      // 获取统计信息
      fetchStats: async () => {
        try {
          const response = await documentService.getDocumentStats();
          set({ stats: response || get().stats });
        } catch (error) {
          console.error('获取统计信息失败:', error);
        }
      },

      // 选择文档
      selectDocument: (document) => {
        set({ selectedDocument: document });
      },

      // 清除选中的文档
      clearSelectedDocument: () => {
        set({ selectedDocument: null });
      },

      // 清除错误
      clearError: () => {
        set({ error: null });
      },

      // 重置状态
      reset: () => {
        set({
          documents: [],
          selectedDocument: null,
          uploadProgress: 0,
          isUploading: false,
          isLoading: false,
          error: null,
          stats: {
            totalDocuments: 0,
            totalChunks: 0,
            processingDocuments: 0
          }
        });
      }
    }),
    { name: 'DocumentStore' }
  )
);

export default useDocumentStore;
