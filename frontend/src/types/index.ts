export interface Conversation {
  id: number
  subject: string
  customerEmail: string
  status: 'OPEN' | 'CLOSED'
  startedAt: string
  closedAt?: string
  messages: Message[]
}

export interface Message {
  id: number
  sender: 'CUSTOMER' | 'AGENT' | 'SYSTEM'
  content: string
  createdAt: string
}

export interface CreateConversationRequest {
  subject: string
  customerEmail: string
  initialMessage: string
}

export interface CreateConversationResponse {
  conversationId: number
}

export interface MessageRequest {
  sender: 'CUSTOMER' | 'AGENT' | 'SYSTEM'
  content: string
}

export interface FaqEntry {
  id: number
  question: string
  answer: string
  category?: string
  keywords?: string[]
}

export interface ApiError {
  timestamp: string
  status: number
  error: string
  message: string
  path: string
}
