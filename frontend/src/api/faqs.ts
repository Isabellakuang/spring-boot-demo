import { apiClient } from './client'
import type { FaqEntry } from '../types'

export const faqsApi = {
  // Get all FAQs
  getAll: async (): Promise<FaqEntry[]> => {
    const response = await apiClient.get<FaqEntry[]>('/knowledge/faqs')
    return response.data
  },

  // Get FAQ by ID
  getById: async (id: number): Promise<FaqEntry> => {
    const response = await apiClient.get<FaqEntry>(`/knowledge/faqs/${id}`)
    return response.data
  },

  // Search FAQs
  search: async (query: string): Promise<FaqEntry | null> => {
    const response = await apiClient.get<FaqEntry | null>('/knowledge/faqs/search', {
      params: { query },
    })
    return response.data
  },
}
