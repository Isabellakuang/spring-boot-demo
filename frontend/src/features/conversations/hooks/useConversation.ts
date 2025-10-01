import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
  appendMessage,
  createConversation,
  fetchConversation
} from '../services/conversationApi';
import type {
  ConversationView,
  CreateConversationRequest,
  CreateConversationResponse,
  MessageRequest
} from '../types/conversation';

export function useConversation(conversationId?: number) {
  const queryClient = useQueryClient();

  const conversationQuery = useQuery<ConversationView>({
    queryKey: ['conversation', conversationId],
    queryFn: () => fetchConversation(conversationId!),
    enabled: Boolean(conversationId)
  });

  const createConversationMutation = useMutation<
    CreateConversationResponse,
    Error,
    CreateConversationRequest
  >({
    mutationFn: createConversation,
    onSuccess: (data) => {
      queryClient.invalidateQueries({
        queryKey: ['conversation', data.conversationId]
      });
    }
  });

  const appendMessageMutation = useMutation<
    ConversationView,
    Error,
    { conversationId: number; message: MessageRequest }
  >({
    mutationFn: ({ conversationId, message }) =>
      appendMessage(conversationId, message),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: ['conversation', variables.conversationId]
      });
    }
  });

  return {
    conversationQuery,
    createConversationMutation,
    appendMessageMutation
  };
}