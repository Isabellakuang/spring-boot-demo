export const API_ROUTES = Object.freeze({
  KNOWLEDGE_FAQ: '/knowledge/faq',
  KNOWLEDGE_SEARCH: '/knowledge/faq/search',
  CONVERSATIONS: '/conversations',
  CONVERSATION_MESSAGES: (id: number | string) => `/conversations/${id}/messages`
});