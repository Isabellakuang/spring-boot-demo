export interface ConversationMessage {
  sender: string;
  content: string;
  timestamp: string;
}

export interface ConversationView {
  conversationId: number;
  subject: string;
  status: string;
  startedAt: string;
  messages: ConversationMessage[];
}

export interface CreateConversationRequest {
  subject: string;
  customerEmail: string;
  initialMessage: string;
}

export interface MessageRequest {
  sender: string;
  content: string;
}

export interface CreateConversationResponse {
  conversationId: number;
}