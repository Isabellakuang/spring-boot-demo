import { axiosClient } from '../../../core/api/axiosClient';
import { API_ROUTES } from '../../../core/constants/routes';
import type {
  ConversationView,
  CreateConversationRequest,
  CreateConversationResponse,
  MessageRequest
} from '../types/conversation';

export async function createConversation(
  payload: CreateConversationRequest
): Promise<CreateConversationResponse> {
  const { data } = await axiosClient.post<CreateConversationResponse>(
    API_ROUTES.CONVERSATIONS,
    payload
  );
  return data;
}

export async function fetchConversation(
  conversationId: number
): Promise<ConversationView> {
  const { data } = await axiosClient.get<ConversationView>(
    `${API_ROUTES.CONVERSATIONS}/${conversationId}`
  );
  return data;
}

export async function appendMessage(
  conversationId: number,
  payload: MessageRequest
): Promise<ConversationView> {
  const { data } = await axiosClient.post<ConversationView>(
    API_ROUTES.CONVERSATION_MESSAGES(conversationId),
    payload
  );
  return data;
}