import { axiosClient } from '../../../core/api/axiosClient';
import type {
  RagQueryRequest,
  RagQueryResponse,
  DocumentIndexRequest,
} from '../types/rag';

export const ragApi = {
  query: async (request: RagQueryRequest): Promise<RagQueryResponse> => {
    const response = await axiosClient.post<RagQueryResponse>(
      '/api/rag/query',
      request
    );
    return response.data;
  },

  indexDocument: async (
    request: DocumentIndexRequest
  ): Promise<{ indexed_chunks: number }> => {
    const response = await axiosClient.post('/api/rag/index', request);
    return response.data;
  },

  batchIndex: async (
    requests: DocumentIndexRequest[]
  ): Promise<Record<string, any>> => {
    const response = await axiosClient.post('/api/rag/batch-index', requests);
    return response.data;
  },

  deleteDocument: async (docId: string): Promise<Record<string, any>> => {
    const response = await axiosClient.delete(`/api/rag/index/${docId}`);
    return response.data;
  },

  getStats: async (): Promise<Record<string, any>> => {
    const response = await axiosClient.get('/api/rag/stats');
    return response.data;
  },

  refreshIndex: async (): Promise<Record<string, any>> => {
    const response = await axiosClient.post('/api/rag/refresh-index');
    return response.data;
  },
};