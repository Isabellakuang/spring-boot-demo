import { apiClient } from './client'
import type {
  Conversation,
  CreateConversationRequest,
  CreateConversationResponse,
  MessageRequest,
} from '../types'

export const conversationsApi = {
  // Create a new conversation
  create: async (data: CreateConversationRequest): Promise<CreateConversationResponse> => {
    const response = await apiClient.post<CreateConversationResponse>('/conversations', data)
    return response.data
  },

  // Get conversation by ID
  getById: async (id: number): Promise<Conversation> => {
    const response = await apiClient.get<Conversation>(`/conversations/${id}`)
    return response.data
  },

  // Add message to conversation
  addMessage: async (id: number, message: MessageRequest): Promise<Conversation> => {
    const response = await apiClient.post<Conversation>(`/conversations/${id}/messages`, message)
    return response.data
  },
}
