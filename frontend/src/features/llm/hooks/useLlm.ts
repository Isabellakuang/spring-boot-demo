import { useMutation, useQuery } from '@tanstack/react-query';
import { llmApi } from '../services/llmApi';
import type { LlmRequest } from '../types/llm';

export const useLlm = () => {
  const generateMutation = useMutation({
    mutationFn: (request: LlmRequest) => llmApi.generate(request),
  });

  const generateWithFallbackMutation = useMutation({
    mutationFn: (request: LlmRequest) => llmApi.generateWithFallback(request),
  });

  const chatMutation = useMutation({
    mutationFn: ({
      conversationId,
      request,
    }: {
      conversationId: string;
      request: LlmRequest;
    }) => llmApi.chat(conversationId, request),
  });

  const modelsQuery = useQuery({
    queryKey: ['llm-models'],
    queryFn: llmApi.getAvailableModels,
    staleTime: 1000 * 60 * 60, // 1 hour
  });

  return {
    generate: generateMutation.mutate,
    generateAsync: generateMutation.mutateAsync,
    isGenerating: generateMutation.isPending,
    generateError: generateMutation.error,
    generateData: generateMutation.data,

    generateWithFallback: generateWithFallbackMutation.mutate,
    isGeneratingWithFallback: generateWithFallbackMutation.isPending,

    chat: chatMutation.mutate,
    chatAsync: chatMutation.mutateAsync,
    isChatting: chatMutation.isPending,
    chatData: chatMutation.data,

    models: modelsQuery.data,
    isLoadingModels: modelsQuery.isLoading,
  };
};