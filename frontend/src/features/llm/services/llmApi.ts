import { axiosClient } from '../../../core/api/axiosClient';
import type { LlmRequest, LlmResponse, LlmModel } from '../types/llm';

export const llmApi = {
  generate: async (request: LlmRequest): Promise<LlmResponse> => {
    const response = await axiosClient.post<LlmResponse>(
      '/api/llm/generate',
      request
    );
    return response.data;
  },

  generateWithFallback: async (request: LlmRequest): Promise<LlmResponse> => {
    const response = await axiosClient.post<LlmResponse>(
      '/api/llm/generate-with-fallback',
      request
    );
    return response.data;
  },

  chat: async (
    conversationId: string,
    request: LlmRequest
  ): Promise<LlmResponse> => {
    const response = await axiosClient.post<LlmResponse>(
      `/api/llm/chat?conversationId=${conversationId}`,
      request
    );
    return response.data;
  },

  getAvailableModels: async (): Promise<Record<string, LlmModel[]>> => {
    const response = await axiosClient.get('/api/llm/models');
    return response.data;
  },

  getUsageStats: async (
    provider?: string,
    startDate?: string,
    endDate?: string
  ): Promise<any> => {
    const params = new URLSearchParams();
    if (provider) params.append('provider', provider);
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);

    const response = await axiosClient.get(
      `/api/llm/usage?${params.toString()}`
    );
    return response.data;
  },
};